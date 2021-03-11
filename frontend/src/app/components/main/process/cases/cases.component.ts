import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { Case } from '../../../../model/case';
import { DataService } from '../../../../services/data.service';
declare var $: any;

@Component({
  selector: 'app-cases',
  templateUrl: './cases.component.html',
  styleUrls: ['./cases.component.css']
})
export class CasesComponent implements OnInit, AfterViewInit {

  // If you access the selected element inside of ngOnInit() you have to set {static: true} and if you don't access selected element in ngOnInit() set {static: false}
  @ViewChild('dataTable', { static: false }) table;
  dataTable: any;

  newCase: Case;
  caseTemplate: Case;

  constructor(
    public dataService: DataService
  ) { }

  ngOnInit(): void {
    console.log("ngOnInit");
    this.dataService.getProcessCases();

    /*
    console.log("$(this.table): "+JSON.stringify($(this.table)));
    this.dtOption = {
      "paging":   false,
      "ordering": false,
      "info":     false
    };
    this.dataTable = $(this.table.nativeElement);
    this.dataTable.DataTable(this.dtOption);
    */
  }

  ngAfterViewInit(): void {
    console.log("ngAfterViewInit");

    this.dataTable = $(this.table.nativeElement);
    this.dataTable.DataTable();
  }

  createNewCase() {
    this.newCase = new Case();

    // Use first case data as template
    if (this.dataService.processCases.length > 0) {
      this.caseTemplate = this.dataService.processCases[0];
    }
    this.newCase.dataMap = this.dataService.processCases[0].dataMap;
  }

  cancelNewCase() {
    this.newCase = null;
  }

  editCase() {
  }

}
