import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CaseExecutionDetailsComponent } from './case-execution-details.component';

describe('CaseExecutionDetailsComponent', () => {
  let component: CaseExecutionDetailsComponent;
  let fixture: ComponentFixture<CaseExecutionDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CaseExecutionDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CaseExecutionDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
