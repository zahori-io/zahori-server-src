import { EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { Component, OnInit } from '@angular/core';
import { CaseExecution } from '../../../../model/caseExecution';
import { DataService } from '../../../../services/data.service';
import { NgbCarousel, NgbCarouselConfig, NgbModal, NgbModalConfig } from '@ng-bootstrap/ng-bootstrap';
import {
  ChartComponent,
  ApexAxisChartSeries,
  ApexChart,
  ApexXAxis,
  ApexDataLabels,
  ApexYAxis,
  ApexLegend,
  ApexFill
} from "ng-apexcharts";
import { Step } from 'src/app/model/step';

declare var HarViewer: any;

export type ChartOptions = {
  series: ApexAxisChartSeries;
  chart: ApexChart;
  xaxis: ApexXAxis;
  dataLabels: ApexDataLabels;
  yaxis: ApexYAxis;
  colors: string[];
  legend: ApexLegend;
  fill: ApexFill;
};


@Component({
  selector: 'app-case-execution-details',
  templateUrl: './case-execution-details.component.html',
  styleUrls: ['./case-execution-details.component.css']
})
export class CaseExecutionDetailsComponent implements OnInit {
  @ViewChild('carousel') carousel: NgbCarousel;
  @Input() caseExecution: CaseExecution;
  @Input() resolutions: Map<string, string>; // <"widthAndHeight", "name">
  @Output() onClose = new EventEmitter<any>();
  downloading = false;
  textFileContent: any;
  file: Blob;
  fileName: string;
  modalMaximized = false;
  stepSelected: Number;

  @ViewChild("historicalCaseExecutionChart") chart: ChartComponent;
  public chartOptions: Partial<ChartOptions>;

  constructor(public dataService: DataService, private modalService: NgbModal, config: NgbCarouselConfig) {
    config.showNavigationArrows = true;
    config.showNavigationIndicators = false;
    config.interval = 15000;
    config.pauseOnFocus = true;
    config.pauseOnHover = true;

    // historicalCaseExecutionChart
    this.chartOptions = {
      series: [],
      chart: {
        type: "area",
        height: 320,
        stacked: true,
        events: {
          selection: function (chart, e) {
            console.log(new Date(e.xaxis.min));
          }
        }
      },
      colors: ["#008FFB", "#00E396", "#CED4DC", "#DAFFC9", "#F97474"],
      dataLabels: {
        enabled: false
      },
      fill: {
        type: "gradient",
        gradient: {
          opacityFrom: 0.6,
          opacityTo: 0.8
        }
      },
      legend: {
        position: "top",
        horizontalAlign: "left"
      },
      xaxis: {
        type: "datetime",
        labels: {
          datetimeUTC: false
        }
      }
    };
    /*
        //labels: {
        //  datetimeUTC: false
        //}
    */
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
    if (filePath.includes('/')) {
      const parts: string[] = filePath.split('/');
      return parts[parts.length - 1];
    }
    if (filePath.includes('\\')) {
      const parts: string[] = filePath.split('\\');
      return parts[parts.length - 1];
    }
  }
  viewStepInModal(index: Number) {
    this.resetModalVariables();
    this.stepSelected = index;
    this.carousel.select('slide-' + index);
  }
  downloadFile(): void {
    const a = document.createElement('a');
    a.href = URL.createObjectURL(this.file);
    a.download = this.fileName;
    // start download
    a.click();
  }

  hide(): void {
    this.caseExecution = new CaseExecution();
    this.onClose.next();
  }

  getAttachmentUrl(attachment: string): string {
    return this.dataService.url + 'process/' + this.dataService.processSelected.processId + '/file?path=' + attachment;
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

  getWidthFromResolution(resolution: string): string {
    return resolution.substr(0, resolution.indexOf('x'));
  }

  getScreenResolutionName(screenResolution: string): string {
    const resolutionName = this.resolutions.get(screenResolution);
    if (resolutionName && resolutionName !== '') {
      return resolutionName;
    } else {
      return screenResolution;
    }
  }

  getCaseExecutions() {
    this.dataService.getCaseExecutions(this.caseExecution.cas.caseId).subscribe(
      (caseExecutions: CaseExecution[]) => {
        let seriesData = this.getChartSeries(caseExecutions);
        console.log("Chart data: " + JSON.stringify(seriesData));
        this.printCaseExecutions(seriesData);
      },
      error => {
        console.log('Error getting case executions for caseId ' + this.caseExecution.cas.caseId + ': ' + error.message);
      }
    );
  }

  getChartSeries(caseExecutions: CaseExecution[]): any[] {
    let steps: {}[] = [];
    let step = {
      "name": "",
      "data": []
    }

    for (let i = 0; i < caseExecutions.length; i++) {
      let caseExecution: CaseExecution = caseExecutions[i];
      for (let j = 0; j < caseExecution.stepsJson.length; j++) {
        let step: Step = caseExecution.stepsJson[j];
        let stepTitle = this.getStepTitle(step);

        if (!steps[j]){
          steps.push({ "name": stepTitle, "data": [] });
          continue;
        }
        if (steps[j] && steps[j]["name"] !== stepTitle) {
          steps.splice(j + 1, 0, { "name": stepTitle, "data": [] });
          continue;
        }
      }
    }
    console.log("Stepsss: " + JSON.stringify(steps));
    ////////////// 
    let series: any[] = [];

    for (let i = 0; i < caseExecutions.length; i++) {
      let caseExecution: CaseExecution = caseExecutions[i];

      for (let j = 0; j < caseExecution.stepsJson.length; j++) {
        let step: Step = caseExecution.stepsJson[j];

        if (!series[j]) {
          let stepSerie = { name: this.getStepTitle(step) };
          series.push(stepSerie);
        }

        let data: any[];
        if (step.status != "Passed"){
          data = [caseExecution.dateTimestamp, null];  
        } else {
            data = [caseExecution.dateTimestamp, step.duration];
        }
        
        if (!series[j]["data"]) {
          series[j]["data"] = [];
        }
        series[j]["data"].push(data);
      }
    }

    return series;
  }

  getStepTitle(step: Step): string{
    let stepTitle = step.messageText;
    const maxStepTitleLength: number = 25;
    if (stepTitle.length > maxStepTitleLength) {
      stepTitle = stepTitle.substring(0, maxStepTitleLength) + '...';
    }
    return stepTitle;
  }

  returnFakeSeries() {
    return [
      {
        "name": "Page loaded",
        "data": [ [1694721815705,2],[1694722815705,5],[1694723815705,3],[1694724815705,4],[1694725815705,7],[1694726815705,3]]
      },
      {
        "name": "Language selected: Español",
        "data": [
          [
            1694721815705,
            8
          ],
          [
            1694722815705,
            6
          ],
          [
            1694723815705,
            10
          ],
          [
            1694724815705,
            null
          ],
          [
            1694725815705,
            6
          ],
          [
            1694726815705,
            7
          ]
        ]
      }
    ];
  }
  printCaseExecutions(seriesData: any[]) {
    this.chartOptions.series = seriesData;
  }

}