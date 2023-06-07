import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { LoginComponent } from './components/login/login.component';
import { MainComponent } from './components/main/main.component';
import { DashboardComponent } from './components/main/dashboard/dashboard.component';
import { ExecutionsComponent } from './components/main/process/executions/executions.component';
import { TriggerComponent } from './components/main/process/trigger/trigger.component';
import { ConfiguratorComponent } from './components/main/process/configurator/configurator.component';
import { CasesComponent } from './components/main/process/cases/cases.component';
import { HelpComponent } from './components/main/help/help.component';
import { ProfileComponent } from './components/main/profile/profile.component';
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
import { SignupComponent } from './components/signup/signup.component';
import { AdminResolutionsComponent } from './components/main/process/process-admin/admin-resolutions/admin-resolutions.component';
import { ProfileChangePasswordComponent } from './components/main/profile/profile-change-password/profile-change-password.component';
import { SchedulerComponent } from './components/main/scheduler/scheduler.component';

const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  {
    path: 'app', component: MainComponent, canActivate: [AuthGuard],
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
        path: 'profile', component: ProfileComponent,
        children: [
          { path: '', component: ProfileChangePasswordComponent },
          { path: 'change-password', component: ProfileChangePasswordComponent },
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
