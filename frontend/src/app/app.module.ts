import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './components/app.component';
import { LoginComponent } from './components/login/login.component';
import { MainComponent } from './components/main/main.component';
import { MenuComponent } from './components/main/menu/menu.component';
import { DashboardComponent } from './components/main/dashboard/dashboard.component';
import { ExecutionsComponent } from './components/main/process/executions/executions.component';
import { TriggerComponent } from './components/main/process/trigger/trigger.component';
import { ConfiguratorComponent } from './components/main/process/configurator/configurator.component';
import { CasesComponent } from './components/main/process/cases/cases.component';
import { HelpComponent } from './components/main/help/help.component';
import { ProfileComponent } from './components/main/profile/profile.component';
import { AdminComponent } from './components/main/admin/admin.component';
import { AdminGroupsComponent } from './components/main/admin/admin-groups/admin-groups.component';
import { AdminPlatformsComponent } from './components/main/admin/admin-platforms/admin-platforms.component';
import { AdminEnvironmentsComponent } from './components/main/admin/admin-environments/admin-environments.component';
import { EnvironmentComponent } from  './components/main/admin/admin-environments/environmentComponent/environmentComponent';
import { AdminExecutionsComponent } from './components/main/admin/admin-executions/admin-executions.component';
import { AdminTagsComponent } from './components/main/admin/admin-tags/admin-tags.component';
import { AdminUsersComponent } from './components/main/admin/admin-users/admin-users.component';
import { AdminTmsComponent } from './components/main/admin/admin-tms/admin-tms.component';
import { AutenticacionService } from './services/autenticacion.service';
import { AuthGuard } from './guards/auth.guard';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { ProcessComponent } from './components/main/process/process.component';
import { ProcessMenuComponent } from './components/main/process/process-menu/process-menu.component';
import { ProcessAdminComponent } from './components/main/process/process-admin/process-admin.component';
import { ProcessAdminMenuComponent } from './components/main/process/process-admin/process-admin-menu/process-admin-menu.component';
import { TeamProcessSelectorComponent } from './components/main/team-process-selector/team-process-selector.component';
import { AdminMenuComponent } from './components/main/admin/admin-menu/admin-menu.component';
import { CaseExecutionDetailsComponent } from './components/main/process/case-execution-details/case-execution-details.component';
import { AuthImagePipe } from './pipes/auth-image-pipe';
import { ClientTeamsComponent } from './components/main/client-teams/client-teams.component';
import { ClientTeamsProcessComponent } from './components/main/client-teams-process/client-teams-process.component';
import { NotificationsComponent } from './components/main/notifications/notifications.component';
import { AlertComponent } from './components/main/admin/admin-environments/alert/alert.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    ExecutionsComponent,
    TriggerComponent,
    ConfiguratorComponent,
    CasesComponent,
    HelpComponent,
    ProfileComponent,
    AdminComponent,
    AdminGroupsComponent,
    AdminPlatformsComponent,
    AdminEnvironmentsComponent,
    EnvironmentComponent,
    AdminExecutionsComponent,
    AdminTagsComponent,
    AdminUsersComponent,
    AdminTmsComponent,
    MenuComponent,
    MainComponent,
    ProcessComponent,
    ProcessMenuComponent,
    ProcessAdminComponent,
    ProcessAdminMenuComponent,
    TeamProcessSelectorComponent,
    AdminMenuComponent,
    CaseExecutionDetailsComponent,
    AuthImagePipe,
    ClientTeamsComponent,
    ClientTeamsProcessComponent,
    NotificationsComponent,
    AlertComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [
    AutenticacionService,
    AuthGuard,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    AuthImagePipe
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
