import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css']
})
export class PaginationComponent implements OnInit {

  @Input()
  totalElements: number;

  @Input()
  totalPages: number;

  @Input()
  currentPage: number;
  
  @Output()
  onPageClick = new EventEmitter<number>();

  constructor() { }

  ngOnInit(): void {
  }

  goToPage(pageNumber: number): void {
    this.onPageClick.emit(pageNumber);
  }
}
