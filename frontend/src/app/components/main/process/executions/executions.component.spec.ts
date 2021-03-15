import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ExecutionsComponent } from './executions.component';

describe('ExecutionsComponent', () => {
  let component: ExecutionsComponent;
  let fixture: ComponentFixture<ExecutionsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ExecutionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ExecutionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
