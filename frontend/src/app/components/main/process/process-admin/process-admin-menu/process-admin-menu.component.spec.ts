import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ProcessAdminMenuComponent } from './process-admin-menu.component';

describe('AdminMenuComponent', () => {
  let component: ProcessAdminMenuComponent;
  let fixture: ComponentFixture<ProcessAdminMenuComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ProcessAdminMenuComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessAdminMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
