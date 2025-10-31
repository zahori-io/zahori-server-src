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
  ApexFill,
  ApexTooltip,
  ApexStroke
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
  tooltip: ApexTooltip,
  stroke: ApexStroke
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
        height: 300,
        stacked: true,
        events: {
          selection: function (chart, e) {
            //console.log(new Date(e.xaxis.min));
          }
        },
        animations: {
          enabled: false
        }
      },
      stroke: {
        curve: 'straight'
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
        position: "bottom",
        horizontalAlign: "center"
      },
      tooltip: {
        enabled: true,
        shared: true,
        intersect: true,
        inverseOrder: true,
        onDatasetHover: {
          highlightDataSeries: true,
        },
        x: {
          show: true,
          format: 'yyyy/MM/dd HH:mm'
        }
      },
      xaxis: {
        type: "datetime",
        labels: {
          datetimeUTC: false
        }
      }
    };
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
    this.maximizeModal();
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

          this.downloading = false;

          // Set HAR content
          const harContent = JSON.parse(content);
          const harviewer = new HarViewer('HarViewer');
          harviewer.loadHar(harContent);
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
        this.printHistoricalChart(seriesData);
      },
      error => {
        console.log('Error getting case executions for caseId ' + this.caseExecution.cas.caseId + ': ' + error.message);
      }
    );
  }

  getStepTitle(step: Step): string {
    let stepTitle = step.messageText;
    const maxStepTitleLength: number = 25;
    if (stepTitle.length > maxStepTitleLength) {
      stepTitle = stepTitle.substring(0, maxStepTitleLength) + '...';
    }
    return stepTitle;
  }

  getChartSeries(caseExecutions: CaseExecution[]): any[] {
    let series: any[] = [];

    if (caseExecutions.length == 0) {
      return series;
    }

    let lastPassedCaseExecutionSteps: Step[] = [];
    let maxSteps = 0;
    let maxStepsIndex = 0;
    for (let i = caseExecutions.length - 1; i >= 0; i--) {
      let caseExecution: CaseExecution = caseExecutions[i];
      if (caseExecution.status == "Passed") {
        lastPassedCaseExecutionSteps = caseExecutions[i].stepsJson;
        break;
      } else {
        if (caseExecution.stepsJson.length > maxSteps) {
          maxSteps = caseExecution.stepsJson.length;
          maxStepsIndex = i;
        }
      }
    }
    // if all cases are failed, take as template the case with more steps
    if (lastPassedCaseExecutionSteps.length == 0) {
      lastPassedCaseExecutionSteps = caseExecutions[maxStepsIndex].stepsJson;
    }

    for (let i = 0; i < lastPassedCaseExecutionSteps.length; i++) {
      let step: Step = lastPassedCaseExecutionSteps[i];
      let serieStep = { "name": this.getStepTitle(step), "data": [] };
      series.push(serieStep);
    }

    // cases
    for (let caseIndex = 0; caseIndex < caseExecutions.length; caseIndex++) {
      let caseExecution: CaseExecution = caseExecutions[caseIndex];

      if (!caseExecution.dateTimestamp) {
        continue;
      }

      // steps serie
      for (let seriesStepIndex = 0; seriesStepIndex < series.length; seriesStepIndex++) {
        let serieStepName = series[seriesStepIndex]["name"];

        // steps
        let data = [caseExecution.dateTimestamp, null];
        for (let stepIndex = 0; stepIndex < caseExecution.stepsJson.length; stepIndex++) {
          let step: Step = caseExecution.stepsJson[stepIndex];
          let stepName = this.getStepTitle(step);
          if (stepName == serieStepName && step.status == "Passed") {
            data = [caseExecution.dateTimestamp, step.duration];
            break;
          }
        }
        series[seriesStepIndex]["data"].push(data);
      }
    }

    // console.log("series: " + JSON.stringify(series));
    return series;
  }

  cleanHistoricalChart() {
    this.printHistoricalChart([]);
  }

  printHistoricalChart(seriesData: any[]) {
    this.chartOptions.series = seriesData;
  }

}