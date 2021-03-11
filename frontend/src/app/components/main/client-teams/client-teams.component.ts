import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Team } from '../../../model/team';
import { DataService } from '../../../services/data.service';
import { ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-client-teams',
  templateUrl: './client-teams.component.html',
  styleUrls: ['./client-teams.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class ClientTeamsComponent implements OnInit {

  constructor(
    public dataService: DataService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit(): void {
  }

  selectTeam(team: Team) {
    this.dataService.teamSelectedInSelector = team;
  }

}
