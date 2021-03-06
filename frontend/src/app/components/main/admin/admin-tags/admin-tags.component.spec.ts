import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AdminTagsComponent } from './admin-tags.component';

describe('AdminTagsComponent', () => {
  let component: AdminTagsComponent;
  let fixture: ComponentFixture<AdminTagsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ AdminTagsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminTagsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
