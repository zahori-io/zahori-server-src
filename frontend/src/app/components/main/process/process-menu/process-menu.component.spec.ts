import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ProcessMenuComponent } from './process-menu.component';

describe('ProcessMenuComponent', () => {
  let component: ProcessMenuComponent;
  let fixture: ComponentFixture<ProcessMenuComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ProcessMenuComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
