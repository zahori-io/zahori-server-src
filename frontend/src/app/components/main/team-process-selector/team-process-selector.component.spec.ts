import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TeamProcessSelectorComponent } from './team-process-selector.component';

describe('TeamProcessSelectorComponent', () => {
  let component: TeamProcessSelectorComponent;
  let fixture: ComponentFixture<TeamProcessSelectorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TeamProcessSelectorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TeamProcessSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
