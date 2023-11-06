import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { LoginComponent } from './components/home/login/login.component';
import { MainComponent } from './components/main/main.component';
import { DashboardComponent } from './components/main/dashboard/dashboard.component';
import { ExecutionsComponent } from './components/main/process/executions/executions.component';
import { TriggerComponent } from './components/main/process/trigger/trigger.component';
import { ConfiguratorComponent } from './components/main/process/configurator/configurator.component';
import { CasesComponent } from './components/main/process/cases/cases.component';
import { HelpComponent } from './components/main/help/help.component';
import { AdminComponent } from './components/main/admin/admin.component';
import { AdminPlatformsComponent } from './components/main/admin/admin-platforms/admin-platforms.component';
import { AdminEnvironmentsComponent } from './components/main/process/process-admin/admin-environments/admin-environments.component';
import { AdminExecutionsComponent } from './components/main/admin/admin-executions/admin-executions.component';
import { AdminTagsComponent } from './components/main/process/process-admin/admin-tags/admin-tags.component';
import { AdminTmsComponent } from './components/main/admin/admin-tms/admin-tms.component';
import { AdminGroupsComponent } from './components/main/admin/admin-groups/admin-groups.component';
import { AdminUsersComponent } from './components/main/admin/admin-users/admin-users.component';
import { AuthGuard } from './guards/auth.guard';
import { ProcessComponent } from './components/main/process/process.component';
import { ProcessAdminComponent } from './components/main/process/process-admin/process-admin.component';
import { SignupComponent } from './components/home/account/signup/signup.component';
import { AdminResolutionsComponent } from './components/main/process/process-admin/admin-resolutions/admin-resolutions.component';
import { AccountComponent } from './components/main/account/account.component';
import { AccountChangePasswordComponent } from './components/main/account/account-change-password/account-change-password.component';
import { AccountChangeEmailComponent } from './components/main/account/account-change-email/account-change-email.component';
import { SchedulerComponent } from './components/main/process/scheduler/scheduler.component';
import { VerifyEmailComponent } from './components/home/account/verify-email/verify-email.component';
import { ForgotPasswordComponent } from './components/home/account/forgot-password/forgot-password.component';
import { HomeComponent } from './components/home/home.component';
import { AccountNotificationsComponent } from './components/main/account/account-notifications/account-notifications.component';

const routes: Routes = [
  { path: '', component: HomeComponent, 
    children: [
      { path: '', component: LoginComponent },
      { path: 'login', component: LoginComponent },
      { path: 'account/forgot-password/:token', component: ForgotPasswordComponent },
      { path: 'account/signup', component: SignupComponent },
      { path: 'account/verify-email/:token', component: VerifyEmailComponent }
    ]
  },
  { path: 'app', component: MainComponent, canActivate: [AuthGuard],
    children: [
      { path: '', component: DashboardComponent },
      { path: 'dashboard', component: DashboardComponent },
      {
        path: 'process', component: ProcessComponent,
        children: [
          { path: 'executions', component: ExecutionsComponent },
          { path: 'trigger', component: TriggerComponent },
          { path: 'scheduler', component: SchedulerComponent },
          { path: 'configurator', component: ConfiguratorComponent },
          { path: 'cases', component: CasesComponent },
          {
            path: 'admin', component: ProcessAdminComponent,
            children: [
              { path: '', component: AdminEnvironmentsComponent },
              { path: 'environments', component: AdminEnvironmentsComponent },
              { path: 'resolutions', component: AdminResolutionsComponent },
              { path: 'tags', component: AdminTagsComponent }
            ]
          }
        ]
      },
      {
        path: 'admin', component: AdminComponent,
        children: [
          { path: '', component: AdminPlatformsComponent },
          { path: 'platforms', component: AdminPlatformsComponent },
          { path: 'executions', component: AdminExecutionsComponent },
          { path: 'tms', component: AdminTmsComponent },
          { path: 'groups', component: AdminGroupsComponent },
          { path: 'users', component: AdminUsersComponent }
        ]
      },
      { path: 'help', component: HelpComponent },
      {
        path: 'account', component: AccountComponent,
        children: [
          { path: '', component: AccountChangeEmailComponent },
          { path: 'change-email', component: AccountChangeEmailComponent },
          { path: 'notifications', component: AccountNotificationsComponent },
          { path: 'change-password', component: AccountChangePasswordComponent }
        ]
      }
    ]
  },
  { path: '**', redirectTo: '', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true, relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
