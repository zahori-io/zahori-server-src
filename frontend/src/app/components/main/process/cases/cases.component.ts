import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
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
    public dataService: DataService
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
        console.log(JSON.stringify(res))
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
      newCase["name"] = "Mi primer caso";

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
      title: 'Eliminar caso: ' + cas["name"],
      text: 'Esta acción no podrá deshacerse cuando se pulse el botón "Guardar" de la página',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Eliminar',
      cancelButtonText: 'Cancelar',
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
      title: 'Añadir campo',
      input: 'text',
      //inputLabel: 'Nombre del campo',
      inputValue: "",
      inputPlaceholder: 'Nombre del campo',
      showCancelButton: true,
      confirmButtonText: 'Añadir',
      cancelButtonText: 'Cancelar',
      //width: '30%',
      //animation: false,
      inputValidator: (value) => {
        if (!value) {
          return 'Escribe el nombre del campo!'
        } else {
          if (tableData.length) {
            var caseRow = tableData[0];
            for (var key of Object.keys(caseRow)) {
              if (key.toLowerCase() === value.toLowerCase()) {
                return "El campo '" + value + "' ya existe, elige otro nombre";
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
      title: 'Cambiar nombre del campo',
      input: 'text',
      //inputLabel: 'Nombre del campo',
      inputValue: fieldName,
      inputPlaceholder: 'Nombre del campo',
      showCancelButton: true,
      confirmButtonText: 'Cambiar',
      cancelButtonText: 'Cancelar',
      inputValidator: (value) => {
        if (!value) {
          return 'Escribe el nombre del campo!'
        } else {
          if (tableData.length) {
            var caseRow = tableData[0];
            for (var key of Object.keys(caseRow)) {
              if (value !== fieldName && key.toLowerCase() === value.toLowerCase()) {
                return "El campo '" + value + "' ya existe, elige otro nombre";
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
      title: 'Eliminar campo: ' + fieldName,
      text: 'Esta acción no podrá deshacerse cuando se pulse el botón "Guardar" de la página',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Eliminar',
      cancelButtonText: 'Cancelar',
      backdrop: `
          rgba(64, 69, 58,0.4)
          left top
          no-repeat`
    }).then((result) => {
      if (result.value) {
        for (const caseRow of tableData) {
          if (fixedFields.includes(fieldName)) {
            alert("No se pueden eliminar columnas fijas");
            return;
          }

          if (caseRow.hasOwnProperty(fieldName)) {
            delete caseRow[fieldName];
            console.log("Field removed");
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
        this.banner = new BannerOptions("Casos guardados", "", SUCCESS_COLOR, true);
      },
      (error) => {
        console.error("Error saving cases: " + error);
        this.banner = new BannerOptions("Error al guardar los casos: " + error, "", ERROR_COLOR, true);
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

  deleteTag(tag: Tag, tags: Tag[]){
    let index : number = tags.indexOf(tag);
    if (index !== -1){
      tags.splice(index, 1);
    }
  }

  setTag(tags: Tag[]){
    let inputOptions = {}

    $.map(this.tags, function(aTag){
      let index : number = tags.indexOf(aTag);
      console.log(JSON.stringify(tags)) 
      console.log(JSON.stringify(aTag)+ "----->" + index) 
      if(index === -1){
        console.log(JSON.stringify(aTag)) 
        inputOptions[aTag.tagId] = aTag.name
      }
    })

    
    Swal.fire({
      title: 'Seleccionar etiqueta',
      input: 'select',
      inputOptions: inputOptions,
      inputPlaceholder: 'Seleccione etiqueta',
      showCancelButton: true,
      inputValidator: (value) => {
        return new Promise((resolve) => {
          tags = tags.concat([inputOptions[value]])
          resolve(null)     
          console.log(JSON.stringify(tags)) 
        })
      }
    })
  }
}
