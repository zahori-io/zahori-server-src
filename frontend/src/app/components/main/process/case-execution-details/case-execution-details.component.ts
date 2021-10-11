import {EventEmitter, Input, Output} from '@angular/core';
import { Component, OnInit } from '@angular/core';
import { CaseExecution } from '../../../../model/caseExecution';
import { DataService } from '../../../../services/data.service';
import {NgbModal, NgbModalConfig} from '@ng-bootstrap/ng-bootstrap';
import {StepsModalComponent} from './steps-modal/steps-modal.component';
declare var HarViewer: any;

@Component({
  selector: 'app-case-execution-details',
  templateUrl: './case-execution-details.component.html',
  styleUrls: ['./case-execution-details.component.css']
})
export class CaseExecutionDetailsComponent implements OnInit {
  @Input() caseExecution: CaseExecution;
  @Output() onClose = new EventEmitter<any>();
  downloading = false;
  textFileContent: any;
  file: Blob;
  fileName: string;
  modalMaximized = false;
  constructor(public dataService: DataService, private modalService: NgbModal, config: NgbModalConfig) {
    config.size = 'xl';
  }
  ngOnInit(): void {
  }
  getTextFile(url: string): void {
    this.resetModalVariables();
    this.downloading = true;
    this.fileName = url.split('/').pop();
    this.dataService.getFile(url).subscribe(
      (data: any) => {
        console.log('Downloading file ' + this.fileName);
        this.file = new Blob([data], { type: 'application/octet-stream' });

        const reader = new FileReader();
        reader.readAsText(this.file, 'UTF-8');
        reader.onload = e => {
          this.textFileContent = e.target.result;
          this.downloading = false;
        };

      },
      error => {
        console.log('Error downloading file ' + this.fileName + ': ' + error.message);
        this.downloading = false;
      },
      () => {
        console.log('File download completed!');
      }
    );
  }

  getHarFile(url: string): void {
    this.resetModalVariables();
    this.downloading = true;
    this.fileName = url.split('/').pop();

    this.dataService.getFile(url).subscribe(
      (data: any) => {
        console.log('Downloading file ' + this.fileName);
        this.file = new Blob([data], { type: 'application/octet-stream' });

        const reader = new FileReader();
        reader.readAsText(this.file);
        reader.onload = e => {
          const content: any = e.target.result;

          // Set HAR content
          const harContent = JSON.parse(content);
          const harviewer = new HarViewer('HarViewer');
          harviewer.loadHar(harContent);

          this.downloading = false;
        };
      },
      error => {
        console.log('Error downloading file ' + this.fileName + ': ' + error.message);
        this.downloading = false;
      },
      () => {
        console.log('File download completed!');
      }
    );
  }

  getFile(url: string): void {
    this.resetModalVariables();
    this.downloading = true;
    this.fileName = url.split('/').pop();

    this.dataService.getFile(url).subscribe(
      (data: any) => {
        console.log('Downloading file ' + this.fileName);
        this.file = new Blob([data], { type: 'application/octet-stream' });
        this.downloadFile();
      },
      error => {
        console.log('Error downloading file ' + this.fileName + ': ' + error.message);
      },
      () => {
        console.log('File download completed!');
        this.downloading = false;
      }
    );
  }

  getFileName(filePath: string): string {
    if (filePath.includes('/')){
      const parts: string[] = filePath.split('/');
      return parts[parts.length - 1];
    }
    if (filePath.includes('\\')){
      const parts: string[] = filePath.split('\\');
      return parts[parts.length - 1];
    }
  }
  viewStepInModal(item: string): void  {
    const modalRef = this.modalService.open(StepsModalComponent);
    modalRef.componentInstance.item = item;
    modalRef.componentInstance.caseExecution = this.caseExecution;
  }
  downloadFile(): void {
    const a = document.createElement('a');
    a.href = URL.createObjectURL(this.file);
    a.download = this.fileName;
    // start download
    a.click();
  }

  hide(): void  {
    this.caseExecution = new CaseExecution();
    this.onClose.next();
  }

  getAttachmentUrl(attachment: string): string {
    return '/api/process/' + this.dataService.processSelected.processId + '/file?path=' + attachment;
  }

  resetModalVariables(): void {
    this.downloading = false;
    this.textFileContent = '';
    const harContentDiv = document.getElementById('HarViewer');
    harContentDiv.innerHTML = '';
    this.file = null;
    this.fileName = '';
    this.modalMaximized = false;
  }
  maximizeModal(): void {
    this.modalMaximized = !this.modalMaximized;
  }
}
