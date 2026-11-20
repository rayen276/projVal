import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbDropdownModule, NgbModalModule, NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';

import { NgApexchartsModule } from 'ng-apexcharts';
import { PsmRoutingModule } from './psm-routing.module';
import { PageHeaderComponent } from './shared/page-header.component';
import { ExportButtonsComponent } from './shared/export-buttons.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ClientsComponent } from './clients/clients.component';
import { ProjetsComponent } from './projets/projets.component';
import { UsersComponent } from './users/users.component';

@NgModule({
  declarations: [
    PageHeaderComponent,
    ExportButtonsComponent,
    DashboardComponent,
    ClientsComponent,
    ProjetsComponent,
    UsersComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    NgbModalModule,
    NgbPaginationModule,
    NgbDropdownModule,
    NgApexchartsModule,
    PsmRoutingModule
  ]
})
export class PsmModule { }
