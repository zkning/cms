import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { SmSqldefineComponent } from './sqldefine/sqldefine.component';
import { SmDataviewComponent } from './dataview/dataview.component';
import { SmDataviewlistComponent } from './dataviewlist/dataviewlist.component';
import { DataViewListResolver } from './resolver/dataviewlist.resolver';
import { RbacDataSourceComponent } from './datasource/datasource.component';

const routes: Routes = [
  { path: 'sqldefine', component: SmSqldefineComponent },
  { path: 'dataview', component: SmDataviewComponent },
  {
    path: 'dataviewlist/:dataViewId',
    component: SmDataviewlistComponent,
    resolve: { dataViewListResolver: DataViewListResolver },
  },
  { path: 'datasource', component: RbacDataSourceComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SmRoutingModule {}
