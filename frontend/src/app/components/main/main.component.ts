import { Component, OnInit } from '@angular/core';
import { DataService } from '../../services/data.service';
import { ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class MainComponent implements OnInit {

  constructor(
    public dataService: DataService
  ) { }

  ngOnInit(): void {
    console.log("init main");
  }
}
