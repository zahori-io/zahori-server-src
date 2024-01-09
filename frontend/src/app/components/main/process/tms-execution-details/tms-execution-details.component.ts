import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { Configuration } from '../../../../model/configuration';
import { Execution } from '../../../../model/execution';
import { DataService } from '../../../../services/data.service';

@Component({
  selector: 'tms-execution-details',
  templateUrl: './tms-execution-details.component.html',
  styleUrls: ['./tms-execution-details.component.css']
})
export class TmsExecutionDetailsComponent implements OnInit, OnChanges {

  @Input() execution: Execution;
  @Input() configuration: Configuration;

  latestTestExecutionIds: Set<string> = new Set<string>();
  latestTestPlanIds: Set<string> = new Set<string>();
  selectedExecId: any = "";

  constructor(
    public dataService: DataService
  ) {}

  ngOnInit(): void {
  }

  ngOnChanges(): void {
    this.latestTestExecutionIds = this.getLatestIds("tmsTestExecutionId", 20);
    this.latestTestPlanIds = this.getLatestIds("tmsTestPlanId", 20);
  }

  getLatestIds(fieldname: string, maxExecutionsToReview: number): Set<string> {
    let ids: Set<string> = new Set<string>();

    // Validate if there are executions in dataService
    if (!this.dataService.processExecutions || this.dataService.processExecutions.data.content.length === 0) {
      return ids;
    }

    for (let i = 0; (i < this.dataService.processExecutions.data.content.length && i < maxExecutionsToReview); i++) {
      if (this.execution 
        && this.execution.configuration.configurationId == this.dataService.processExecutions.data.content[i].configuration.configurationId
        && this.dataService.processExecutions.data.content[i][fieldname] 
        && this.dataService.processExecutions.data.content[i][fieldname] != "") {
        ids.add(this.dataService.processExecutions.data.content[i][fieldname]);
      }
    }
    return ids;
  }

}
