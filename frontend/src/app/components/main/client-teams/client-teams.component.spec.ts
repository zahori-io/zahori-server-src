import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ClientTeamsComponent } from './client-teams.component';

describe('ClientTeamsComponent', () => {
  let component: ClientTeamsComponent;
  let fixture: ComponentFixture<ClientTeamsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ClientTeamsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClientTeamsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
