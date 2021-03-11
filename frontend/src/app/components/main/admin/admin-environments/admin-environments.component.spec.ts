import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminEnvironmentsComponent } from './admin-environments.component';

describe('AdminEnvironmentsComponent', () => {
  let component: AdminEnvironmentsComponent;
  let fixture: ComponentFixture<AdminEnvironmentsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminEnvironmentsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminEnvironmentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
