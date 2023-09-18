import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './components/app.component';
import { LoginComponent } from './components/home/login/login.component';
import { MainComponent } from './components/main/main.component';
import { MenuComponent } from './components/main/menu/menu.component';
import { DashboardComponent } from './components/main/dashboard/dashboard.component';
import { ExecutionsComponent } from './components/main/process/executions/executions.component';
import { TriggerComponent } from './components/main/process/trigger/trigger.component';
import { ConfiguratorComponent } from './components/main/process/configurator/configurator.component';
import { ConfiguratorFormComponent } from './components/main/process/configurator/configuratorForm/configuratorForm.component';
import { CasesComponent } from './components/main/process/cases/cases.component';
import { HelpComponent } from './components/main/help/help.component';
import { AdminComponent } from './components/main/admin/admin.component';
import { AdminGroupsComponent } from './components/main/admin/admin-groups/admin-groups.component';
import { AdminPlatformsComponent } from './components/main/admin/admin-platforms/admin-platforms.component';
import { AdminEnvironmentsComponent } from './components/main/process/process-admin/admin-environments/admin-environments.component';
import { EnvironmentComponent } from './components/main/process/process-admin/admin-environments/environmentComponent/environmentComponent';
import { AdminExecutionsComponent } from './components/main/admin/admin-executions/admin-executions.component';
import { AdminTagsComponent } from './components/main/process/process-admin/admin-tags/admin-tags.component';
import { tagComponent } from './components/main/process/process-admin/admin-tags/tagComponent/tagComponent';
import { AdminUsersComponent } from './components/main/admin/admin-users/admin-users.component';
import { AdminTmsComponent } from './components/main/admin/admin-tms/admin-tms.component';
import { DataService } from './services/data.service';
import { AutenticacionService } from './services/autenticacion.service';
import { I18nService } from './services/i18n.service';
import { AuthGuard } from './guards/auth.guard';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { I18nInterceptor } from './interceptors/i18n.interceptor';
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
import { BannerComponent } from './components/utils/banner/banner.component';
import { MatChipsModule } from '@angular/material/chips';
import { SignupComponent } from './components/home/account/signup/signup.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { AdminResolutionsComponent } from './components/main/process/process-admin/admin-resolutions/admin-resolutions.component';
import { ResolutionComponentComponent } from './components/main/process/process-admin/admin-resolutions/resolution-component/resolution-component.component';
import { NgMultiSelectDropDownModule } from 'ng-multiselect-dropdown';
import { SortDirective } from './directive/sort.directive';
import { NgCircleProgressModule } from 'ng-circle-progress';
import { TmsExecutionDetailsComponent } from './components/main/process/tms-execution-details/tms-execution-details.component';
import { AccountComponent } from './components/main/account/account.component';
import { AccountMenuComponent } from './components/main/account/account-menu/account-menu.component';
import { AccountChangePasswordComponent } from './components/main/account/account-change-password/account-change-password.component';
import { AccountChangeEmailComponent } from './components/main/account/account-change-email/account-change-email.component';
import { PaginationComponent } from './components/utils/pagination/pagination.component';
import { SchedulerComponent } from './components/main/process/scheduler/scheduler.component';
import { VerifyEmailComponent } from './components/home/account/verify-email/verify-email.component';
import { ModalComponent } from './components/utils/modal/modal.component';
import { ForgotPasswordComponent } from './components/home/account/forgot-password/forgot-password.component';
import { HomeComponent } from './components/home/home.component';
import { NgApexchartsModule } from 'ng-apexcharts';

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
    AccountComponent,
    AccountComponent,
    AccountMenuComponent,
    AccountChangePasswordComponent,
    AccountChangeEmailComponent,
    VerifyEmailComponent,
    AdminComponent,
    AdminGroupsComponent,
    AdminPlatformsComponent,
    AdminEnvironmentsComponent,
    EnvironmentComponent,
    AdminExecutionsComponent,
    AdminTagsComponent,
    tagComponent,
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
    BannerComponent,
    ConfiguratorFormComponent,
    SignupComponent,
    AdminResolutionsComponent,
    ResolutionComponentComponent,
    SortDirective,
    TmsExecutionDetailsComponent,
    PaginationComponent,
    SchedulerComponent,
    ModalComponent,
    ForgotPasswordComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    NgSelectModule,
    MatChipsModule,
    ReactiveFormsModule,
    NgbModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    NgMultiSelectDropDownModule,
    NgCircleProgressModule.forRoot({
      // defaults:
      backgroundPadding: 0,
      showImage: false,
      animationDuration: 0,
      showBackground: false,
      showUnits: false,
      showZeroOuterStroke: false
    }),
    NgApexchartsModule
  ],
  providers: [
    DataService,
    AutenticacionService,
    I18nService,
    AuthGuard,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: I18nInterceptor, multi: true },
    AuthImagePipe
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

// required for AOT compilation
export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http, '/zahori/assets/i18n/');
}
