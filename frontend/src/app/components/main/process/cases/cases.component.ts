import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Tag } from 'src/app/model/tag';
import { BannerOptions } from 'src/app/utils/banner/banner';
import Swal from 'sweetalert2';
import { DataService } from '../../../../services/data.service';
import { Case } from '../../../../model/case';

const SUCCESS_COLOR = 'alert alert-success';
const ERROR_COLOR = 'alert alert-danger';

@Component({
  selector: 'app-cases',
  templateUrl: './cases.component.html',
  styleUrls: ['./cases.component.css']
})
export class CasesComponent implements OnInit {

  fixedFields: string[] = [];
  tags: Tag[];
  tableData: {}[] = [];
  loading = true;
  banner: BannerOptions;
  tag: Tag;
  isHidden = true;

  constructor(
    public dataService: DataService,
    private translate: TranslateService
  ) { }

  ngOnInit(): void {
    console.log('ngOnInit');
    this.getCases();
    this.banner = new BannerOptions();
    this.getTags();
  }

  getTags(): void {
    this.dataService.getTags(String(this.dataService.processSelected.processId)).subscribe(
      (res: any) => {
        this.tags = res;
      });
  }

  getCases(): void {
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

  createNewCase(): void {
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

  copyCase(cse: Case, index: number): void {
    let newCase: Case;
    newCase = JSON.parse(JSON.stringify(this.tableData[0]));
    newCase.clientTags = cse.clientTags;
    Object.keys(newCase).forEach(prop => {
      newCase[prop] = cse[prop];
    });
    newCase.active = true;
    newCase.caseId = 0;
    this.tableData.splice(index + 1, 0, newCase);
  }

  removeCase(tableData: {}[], cas: Case): void {
    Swal.fire({
      title: this.translate.instant('main.process.cases.removeCase.title') + cas.name,
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
        if (!cas.caseId || cas.caseId <= 0) {
          const index = tableData.indexOf(cas);
          tableData.splice(index, 1);
        } else { // case present in database (valid caseId)
          // logical remove:
          cas.active = false;
        }
      }
    });
  }

  async addField(tableData): Promise<void> {
    const { value: newFieldName } = await Swal.fire({
      title: this.translate.instant('main.process.cases.addField.title'),
      input: 'text',
      // inputLabel: 'Nombre del campo',
      inputValue: '',
      inputPlaceholder: this.translate.instant('main.process.cases.addField.placeholder'),
      showCancelButton: true,
      confirmButtonText: this.translate.instant('main.process.cases.addField.confirmButton'),
      cancelButtonText: this.translate.instant('main.process.cases.addField.cancelButton'),
      // width: '30%',
      // animation: false,
      inputValidator: (value) => {
        if (!value) {
          return this.translate.instant('main.process.cases.addField.emptyNameWarning');
        } else {
          if (tableData.length) {
            const caseRow = tableData[0];
            for (const key of Object.keys(caseRow)) {
              if (key.toLowerCase() === value.toLowerCase()) {
                return this.translate.instant('main.process.cases.addField.duplicatedName', { fieldName: value });
              }
            }
          }
        }
      }
    });

    if (newFieldName) {
      for (const caseRow of tableData) {
        caseRow[newFieldName] = '';
      }
    }
  }

  async editField(tableData, fieldName: string): Promise<void> {
    const { value: newFieldName } = await Swal.fire({
      title: this.translate.instant('main.process.cases.editField'),
      input: 'text',
      inputValue: fieldName,
      inputPlaceholder: this.translate.instant('main.process.cases.addField.placeholder'),
      showCancelButton: true,
      confirmButtonText: this.translate.instant('main.process.cases.editFieldSaveButton'),
      cancelButtonText: this.translate.instant('main.process.cases.addField.cancelButton'),
      inputValidator: (value) => {
        if (!value) {
          return this.translate.instant('main.process.cases.addField.emptyNameWarning');
        } else {
          if (tableData.length) {
            const caseRow = tableData[0];
            for (const key of Object.keys(caseRow)) {
              if (value !== fieldName && key.toLowerCase() === value.toLowerCase()) {
                return this.translate.instant('main.process.cases.addField.duplicatedName', { fieldName: value });
              }
            }
          }
        }
      }
    });

    if (newFieldName && newFieldName !== fieldName) {
      for (const caseRow of tableData) {
        caseRow[newFieldName] = caseRow[fieldName];
        delete caseRow[fieldName];
      }
    }
  }

  removeField(tableData, fieldName: string, fixedFields: string[]): void {
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
    const parsedCases: {}[] = [];

    for (const row of casesFromRequest) {
      const variableFields = JSON.parse(JSON.stringify(row.data));
      const rowFixedFields = JSON.parse(JSON.stringify(row));
      delete rowFixedFields.data;

      // Store fixed fields
      if (fixedFields.length === 0) {
        Object.keys(rowFixedFields).forEach(prop => {
          fixedFields.push(prop);
        });
      }

      const caseRow = {};
      // Fixed fields
      Object.keys(rowFixedFields).forEach(prop => {
        caseRow[prop] = rowFixedFields[prop];
      });
      // Variable fields
      Object.keys(variableFields).forEach(prop => {
        caseRow[prop] = variableFields[prop];
      });

      parsedCases.push(caseRow);
    }

    return parsedCases;
  }


  formatCases(casesFromTable: any, fixedFields: string[]): any {
    const casesFromTableCopy = JSON.parse(JSON.stringify(casesFromTable));
    const formattedCases = [];

    for (const caseRow of casesFromTableCopy) {
      const formattedCase = {
        data: {},
        dataMap: undefined
      };
      fixedFields.forEach(
        field => {
          formattedCase[field] = caseRow[field];
          delete caseRow[field];
        }
      );

      // Variable fields
      Object.keys(caseRow).forEach(prop => {
        formattedCase.data[prop] = caseRow[prop];
      });

      // TO-DO: remove dataMap. Also in server and process sides
      formattedCase.dataMap = JSON.parse(JSON.stringify(formattedCase.data));

      formattedCases.push(formattedCase);
    }

    return formattedCases;
  }

  saveCases(): void {
    const formattedCases = this.formatCases(this.tableData, this.fixedFields);
    console.log(formattedCases);
    this.dataService.saveCases(formattedCases).subscribe(
      (cases) => {
        this.tableData = this.parseCases(JSON.parse(cases), this.fixedFields);
        console.log('cases saved!');
        this.banner = new BannerOptions(this.translate.instant('main.process.cases.saveSuccess'), '', SUCCESS_COLOR, true);
      },
      (error) => {
        console.error('Error saving cases: ' + error.message);
        this.banner = new BannerOptions(this.translate.instant('main.process.cases.saveError') + error.message, '', ERROR_COLOR, true);
      }
    );
  }

  isVariableField(fieldName: string): boolean {
    return !this.fixedFields.includes(fieldName);
  }

  // Function to avoid losing focus in input field when using keyvalue pipe:
  // https://stackoverflow.com/questions/63461339/ng-model-not-updating-object-from-input-tag-inside-ngfor-with-keyvalue-pipe
  avoidLosingFocus(item): void {
    return item;
  }

  closeBanner(): void {
    this.banner = new BannerOptions();
  }

  thereAreActiveCases(): boolean {
    if (this.tableData.length === 0) {
      return false;
    }

    for (const caseRow of this.tableData) {
      // @ts-ignore
      if (caseRow.active === true) {
        return true;
      }
    }

    return false;
  }

}
