import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TmsExecutionDetailsComponent } from './tms-execution-details.component';

describe('TmsExecutionDetailsComponent', () => {
  let component: TmsExecutionDetailsComponent;
  let fixture: ComponentFixture<TmsExecutionDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TmsExecutionDetailsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TmsExecutionDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
