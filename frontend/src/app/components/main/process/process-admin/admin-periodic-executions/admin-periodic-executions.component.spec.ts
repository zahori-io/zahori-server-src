import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPeriodicExecutionsComponent } from './admin-periodic-executions.component';

describe('AdminPeriodicExecutionsComponent', () => {
  let component: AdminPeriodicExecutionsComponent;
  let fixture: ComponentFixture<AdminPeriodicExecutionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdminPeriodicExecutionsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminPeriodicExecutionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
