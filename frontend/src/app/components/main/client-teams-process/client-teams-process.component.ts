import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Process } from '../../../model/process';
import { DataService } from '../../../services/data.service';

@Component({
  selector: 'app-client-teams-process',
  templateUrl: './client-teams-process.component.html',
  styleUrls: ['./client-teams-process.component.css']
})
export class ClientTeamsProcessComponent implements OnInit {

  @Output()
  onProcessSelection = new EventEmitter<any>();

  constructor(
    public dataService: DataService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit(): void {
  }

  selectProcess(process: Process) {
    this.dataService.setTeam(this.dataService.teamSelectedInSelector);
    this.dataService.setProcess(process);
    this.onProcessSelection.next();

    this.getProcessExecutions();
    this.dataService.getProcessCases();
    this.dataService.getProcessConfigurations();

    // if process was selected from dashboard -> navigate to executions
    if (this.activatedRoute.snapshot.url.length == 0 || "dashboard" == this.activatedRoute.snapshot.url[0].path) {
      this.router.navigate(['/app/process/executions']);
    }
  }

  getProcessExecutions() {
    this.dataService.getExecutionsPageable(0, 10).subscribe(
      executions => {
        this.dataService.processExecutions = executions;
      }
    );
  }

}

