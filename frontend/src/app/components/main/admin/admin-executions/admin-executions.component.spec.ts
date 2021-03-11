import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminExecutionsComponent } from './admin-executions.component';

describe('AdminExecutionsComponent', () => {
  let component: AdminExecutionsComponent;
  let fixture: ComponentFixture<AdminExecutionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminExecutionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminExecutionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
