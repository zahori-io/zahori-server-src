import { EventEmitter, Input, Output } from '@angular/core';
import { Component, OnInit } from '@angular/core';
import { CaseExecution } from '../../../../model/caseExecution';
import { DataService } from '../../../../services/data.service';
import { ViewEncapsulation } from '@angular/core';
import { Step } from '../../../../model/step';
declare var $: any;
declare var HarViewer: any;

@Component({
  selector: 'app-case-execution-details',
  templateUrl: './case-execution-details.component.html',
  styleUrls: ['./case-execution-details.component.css']
})
export class CaseExecutionDetailsComponent implements OnInit {

  @Input() caseExecution: CaseExecution;
  @Output() onClose = new EventEmitter<any>(); 

  downloading: boolean = false;
  textFileContent: any;
  file: Blob;
  fileName: string;
  stepModal: Step;
  modalMaximized: boolean = false;

  constructor(
    public dataService: DataService
  ) { }

  ngOnInit(): void {
    console.log("init CaseExecutionDetailsComponent");
  }

  getTextFile(url: string) {
    this.resetModalVariables();
    this.downloading = true;
    this.fileName = url.split("/").pop();

    this.dataService.getFile(url).subscribe(
      (data: any) => {
        console.log("Downloading file " + this.fileName);
        this.file = new Blob([data], { type: 'application/octet-stream' });

        var reader = new FileReader();
        reader.readAsText(this.file, 'ISO-8859-1');
        reader.onload = e => {
          this.textFileContent = e.target.result;
          this.downloading = false;
        };

      },
      error => {
        console.log("Error downloading file " + this.fileName + ": " + error.message)
        this.downloading = false;
      },
      () => {
        console.log('File download completed!')
      }
    );
  }

  getHarFile(url: string) {
    this.resetModalVariables();
    this.downloading = true;
    this.fileName = url.split("/").pop();

    this.dataService.getFile(url).subscribe(
      (data: any) => {
        console.log("Downloading file " + this.fileName);
        this.file = new Blob([data], { type: 'application/octet-stream' });

        var reader = new FileReader();
        reader.readAsText(this.file);
        reader.onload = e => {
          var content: any = e.target.result;

          //Set HAR content
          var harContent = JSON.parse(content);
          var harviewer = new HarViewer('HarViewer');
          harviewer.loadHar(harContent);

          this.downloading = false;
        };
      },
      error => {
        console.log("Error downloading file " + this.fileName + ": " + error.message)
        this.downloading = false;
      },
      () => {
        console.log('File download completed!')
      }
    );
  }

  getFile(url: string) {
    this.resetModalVariables();
    this.downloading = true;
    this.fileName = url.split("/").pop();

    this.dataService.getFile(url).subscribe(
      (data: any) => {
        console.log("Downloading file " + this.fileName);
        this.file = new Blob([data], { type: 'application/octet-stream' });
        this.downloadFile();
      },
      error => {
        console.log("Error downloading file " + this.fileName + ": " + error.message)
      },
      () => {
        console.log('File download completed!')
        this.downloading = false;
      }
    );
  }

  getFileName(filePath: string): string {
    if (filePath.includes("/")){
      let parts:string[] = filePath.split("/");
      return parts[parts.length-1];
    }
    if (filePath.includes("\\")){
      let parts:string[] = filePath.split("\\");
      return parts[parts.length-1];
    }
  }

  viewStepInModal(step: Step) {
    this.resetModalVariables();
    this.stepModal = step;
  }

  downloadFile() {
    var a = document.createElement("a");
    a.href = URL.createObjectURL(this.file);
    a.download = this.fileName;
    // start download
    a.click();
  }

  hide() {
    this.caseExecution = new CaseExecution();
    this.onClose.next();
  }

  getAttachmentUrl(attachment: string): string {
    return '/api/process/' + this.dataService.processSelected.processId + '/file?path=' + attachment;
  }

  resetModalVariables() {
    this.downloading = false;
    this.textFileContent = "";
    var harContentDiv = document.getElementById("HarViewer");
    harContentDiv.innerHTML = "";
    this.file = null;
    this.fileName = "";
    this.stepModal = null;
    this.modalMaximized = false;
  }

  maximizeModal() {
    this.modalMaximized = !this.modalMaximized;
  }
}
