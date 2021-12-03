import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Tag } from 'src/app/model/tag';
import { BannerOptions } from 'src/app/utils/banner/banner';
import Swal from 'sweetalert2';
import { DataService } from '../../../../services/data.service';

const SUCCESS_COLOR: string = "alert alert-success";
const ERROR_COLOR: string = "alert alert-danger";

@Component({
  selector: 'app-cases',
  templateUrl: './cases.component.html',
  styleUrls: ['./cases.component.css']
})
export class CasesComponent implements OnInit {

  fixedFields: string[] = [];
  tags : Tag[];
  tableData: {}[] = [];
  loading: boolean = true;
  banner: BannerOptions;
  tag : Tag;
  isHidden : Boolean = true

  constructor(
    public dataService: DataService,
    private translate: TranslateService
  ) { }

  ngOnInit(): void {
    console.log("ngOnInit");
    this.getCases();
    this.banner = new BannerOptions();
    this.getTags()
  }

  getTags(){
    this.dataService.getTags(String(this.dataService.processSelected.processId)).subscribe(
      (res : any) => {
        this.tags = res;
      });
  }

  getCases() {
    this.loading = true;
    this.dataService.getCasesJson().subscribe(
      (cases) => {
        this.tableData = this.parseCases(JSON.parse(cases), this.fixedFields);
      },
      (error) => { },
      () => {
        this.loading = false;
      }
    );
  }

  createNewCase() {
    var newCase = {};
    if (this.tableData.length == 0) {
      newCase["name"] = this.translate.instant('main.process.cases.firstCaseName');

      this.fixedFields.push("caseId");
      this.fixedFields.push("name");
      this.fixedFields.push("clientTags");
      this.fixedFields.push("active");
    } else {
      newCase = JSON.parse(JSON.stringify(this.tableData[0]));
      Object.keys(newCase).forEach(function (prop) {
        newCase[prop] = "";
      });
    }
    newCase["active"] = true;
    newCase["clientTags"] = [];
    this.tableData.unshift(newCase);
  }

  removeCase(tableData: {}[], cas: {}) {
    Swal.fire({
      title: this.translate.instant('main.process.cases.removeCase.title') + cas["name"],
      text: this.translate.instant('main.process.cases.removeCase.warning'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: this.translate.instant('main.process.cases.removeCase.confirmButton'),
      cancelButtonText: this.translate.instant('main.process.cases.removeCase.cancelButton'),
      backdrop: `
          rgba(64, 69, 58,0.4)
          left top
          no-repeat`
    }).then((result) => {
      if (result.value) {
        // remove new case (case still not present in database)
        if (!cas["caseId"] || cas["caseId"] <= 0) {
          let index = tableData.indexOf(cas);
          tableData.splice(index, 1);
        } else { // case present in database (valid caseId)
          // logical remove:
          cas["active"] = false;
        }
      }
    });
  }

  async addField(tableData) {
    const { value: newFieldName } = await Swal.fire({
      title: this.translate.instant('main.process.cases.addField.title'),
      input: 'text',
      //inputLabel: 'Nombre del campo',
      inputValue: "",
      inputPlaceholder: this.translate.instant('main.process.cases.addField.placeholder'),
      showCancelButton: true,
      confirmButtonText: this.translate.instant('main.process.cases.addField.confirmButton'),
      cancelButtonText: this.translate.instant('main.process.cases.addField.cancelButton'),
      //width: '30%',
      //animation: false,
      inputValidator: (value) => {
        if (!value) {
          return this.translate.instant('main.process.cases.addField.emptyNameWarning')
        } else {
          if (tableData.length) {
            var caseRow = tableData[0];
            for (var key of Object.keys(caseRow)) {
              if (key.toLowerCase() === value.toLowerCase()) {
                return this.translate.instant('main.process.cases.addField.duplicatedName', {fieldName: value});
              }
            }
          }
        }
      }
    })

    if (newFieldName) {
      for (const caseRow of tableData) {
        caseRow[newFieldName] = "";
      }
    }
  }

  async editField(tableData, fieldName: string) {
    const { value: newFieldName } = await Swal.fire({
      title: this.translate.instant('main.process.cases.editField'),
      input: 'text',
      //inputLabel: 'Nombre del campo',
      inputValue: fieldName,
      inputPlaceholder: this.translate.instant('main.process.cases.addField.placeholder'),
      showCancelButton: true,
      confirmButtonText: this.translate.instant('main.process.cases.editFieldSaveButton'),
      cancelButtonText: this.translate.instant('main.process.cases.addField.cancelButton'),
      inputValidator: (value) => {
        if (!value) {
          return this.translate.instant('main.process.cases.addField.emptyNameWarning')
        } else {
          if (tableData.length) {
            var caseRow = tableData[0];
            for (var key of Object.keys(caseRow)) {
              if (value !== fieldName && key.toLowerCase() === value.toLowerCase()) {
                return this.translate.instant('main.process.cases.addField.duplicatedName', {fieldName: value});
              }
            }
          }
        }
      }
    })

    if (newFieldName && newFieldName !== fieldName) {
      for (const caseRow of tableData) {
        caseRow[newFieldName] = caseRow[fieldName];
        delete caseRow[fieldName];
      }
    }
  }

  removeField(tableData, fieldName: string, fixedFields: string[]) {
    Swal.fire({
      title: this.translate.instant('main.process.cases.removeField.title') + fieldName,
      text: this.translate.instant('main.process.cases.removeField.warning'),
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: this.translate.instant('main.process.cases.removeField.confirmButton'),
      cancelButtonText: this.translate.instant('main.process.cases.removeField.cancelButton'),
      backdrop: `
          rgba(64, 69, 58,0.4)
          left top
          no-repeat`
    }).then((result) => {
      if (result.value) {
        for (const caseRow of tableData) {
          if (fixedFields.includes(fieldName)) {
            alert(this.translate.instant('main.process.cases.removeField.warningFixedFields'));
            return;
          }

          if (caseRow.hasOwnProperty(fieldName)) {
            delete caseRow[fieldName];
            console.log(this.translate.instant('main.process.cases.removeField.confirmation'));
          }
        }
      }
    });
  }

  parseCases(casesFromRequest: any, fixedFields: string[]): {}[] {
    var parsedCases: {}[] = [];

    for (const row of casesFromRequest) {
      var variableFields = JSON.parse(JSON.stringify(row.data));
      var rowFixedFields = JSON.parse(JSON.stringify(row));
      delete rowFixedFields["data"];

      // Store fixed fields
      if (fixedFields.length == 0) {
        Object.keys(rowFixedFields).forEach(function (prop) {
          fixedFields.push(prop);
        });
      }

      var caseRow = {};
      // Fixed fields
      Object.keys(rowFixedFields).forEach(function (prop) {
        caseRow[prop] = rowFixedFields[prop];
      });
      // Variable fields
      Object.keys(variableFields).forEach(function (prop) {
        caseRow[prop] = variableFields[prop];
      });

      parsedCases.push(caseRow);
    }

    return parsedCases;
  }


  formatCases(casesFromTable: any, fixedFields: string[]) {
    var casesFromTableCopy = JSON.parse(JSON.stringify(casesFromTable));
    console.log(casesFromTable);
    var formattedCases = [];

    for (const caseRow of casesFromTableCopy) {
      var formattedCase = { "data": {} };
      fixedFields.forEach(
        field => {
          formattedCase[field] = caseRow[field];
          delete caseRow[field];
        }
      );

      // Variable fields
      Object.keys(caseRow).forEach(function (prop) {
        formattedCase.data[prop] = caseRow[prop];
      });
      
      // TO-DO: remove dataMap. Also in server and process sides
      formattedCase["dataMap"] = JSON.parse(JSON.stringify(formattedCase.data));

      formattedCases.push(formattedCase);
    }

    return formattedCases;
  }

  saveCases() {
    var formattedCases = this.formatCases(this.tableData, this.fixedFields);
    this.dataService.saveCases(formattedCases).subscribe(
      (cases) => {
        this.tableData = this.parseCases(JSON.parse(cases), this.fixedFields);
        console.log("cases saved!");
        this.banner = new BannerOptions(this.translate.instant('main.process.cases.saveSuccess'), "", SUCCESS_COLOR, true);
      },
      (error) => {
        console.error("Error saving cases: " + error.message);
        this.banner = new BannerOptions(this.translate.instant('main.process.cases.saveError') + error.message, "", ERROR_COLOR, true);
      }
    );
  }

  isVariableField(fieldName: string): boolean {
    return !this.fixedFields.includes(fieldName);
  }

  // Function to avoid losing focus in input field when using keyvalue pipe: 
  // https://stackoverflow.com/questions/63461339/ng-model-not-updating-object-from-input-tag-inside-ngfor-with-keyvalue-pipe
  avoidLosingFocus(item) {
    return item;
  }

  closeBanner() {
    this.banner = new BannerOptions;
  }

  thereAreActiveCases() {
    if (this.tableData.length === 0) {
      return false;
    }

    for (const caseRow of this.tableData) {
      if (caseRow["active"] === true) {
        return true;
      }
    }

    return false;
  }

}
