import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AdminTmsComponent } from './admin-tms.component';

describe('AdminTmsComponent', () => {
  let component: AdminTmsComponent;
  let fixture: ComponentFixture<AdminTmsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminTmsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminTmsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
