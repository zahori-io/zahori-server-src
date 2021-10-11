import {Component, OnInit, Input, ViewChild, AfterViewInit} from '@angular/core';
import {DataService} from '../../../../../services/data.service';
import {NgbActiveModal, NgbCarousel, NgbCarouselConfig, NgbModalConfig} from '@ng-bootstrap/ng-bootstrap';
@Component({
  selector: 'app-steps-modal',
  templateUrl: './steps-modal.component.html',
  styleUrls: ['./steps-modal.component.css']
})
export class StepsModalComponent implements OnInit, AfterViewInit {
  @ViewChild('carousel') carousel: NgbCarousel;
  @Input() caseExecution;
  @Input() item;
  modalMaximized = false;
  constructor(public activeModal: NgbActiveModal, public dataService: DataService,config: NgbCarouselConfig) {
    config.showNavigationArrows = true;
    config.showNavigationIndicators = false;
    config.interval = 15000;
    config.pauseOnFocus = true;
    config.pauseOnHover = true;
  }
  ngOnInit(): void {
    console.log('incio');
  }
  ngAfterViewInit(): void {
    console.log('Item: ' + this.item);
    this.carousel.select('slide-' + this.item);
  }
  maximizeModal(): void {
    this.modalMaximized = !this.modalMaximized;
  }
  getAttachmentUrl(attachment: string): string {
    return '/api/process/' + this.dataService.processSelected.processId + '/file?path=' + attachment;
  }
}
