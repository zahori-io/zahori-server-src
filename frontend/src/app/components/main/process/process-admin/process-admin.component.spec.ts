import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ProcessAdminComponent } from './process-admin.component';

describe('ProcessAdminComponent', () => {
  let component: ProcessAdminComponent;
  let fixture: ComponentFixture<ProcessAdminComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ProcessAdminComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
