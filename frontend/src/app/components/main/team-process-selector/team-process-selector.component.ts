import { Component, OnInit } from '@angular/core';
import { ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-team-process-selector',
  templateUrl: './team-process-selector.component.html',
  styleUrls: ['./team-process-selector.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class TeamProcessSelectorComponent implements OnInit {

  constructor(
  ) { }

  ngOnInit(): void {
    console.log("init team-process-selector");
  }

}
