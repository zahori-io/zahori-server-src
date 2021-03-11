import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminTmsComponent } from './admin-tms.component';

describe('AdminTmsComponent', () => {
  let component: AdminTmsComponent;
  let fixture: ComponentFixture<AdminTmsComponent>;

  beforeEach(async(() => {
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
