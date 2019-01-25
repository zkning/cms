import { NgModule } from '@angular/core';
import { SharedModule } from '@shared/shared.module';
import { SmRoutingModule } from './sm-routing.module';
import { SmSqldefineComponent } from './sqldefine/sqldefine.component';
import { SmSqldefineEditComponent } from './sqldefine/edit/edit.component';
import { SmDataviewComponent } from './dataview/dataview.component';
import { SmDataviewEditComponent } from './dataview/edit/edit.component';
import { SmBtneditComponent } from './btnedit/btnedit.component';
import { PatternComponent } from './pattern/pattern.component';
import { SqlSelectorComponent } from './sqlselector/sqlselector.component';
import { SmDataviewlistComponent } from './dataviewlist/dataviewlist.component';
import { DataViewListResolver } from './resolver/dataviewlist.resolver';
import { DataViewEditComponent } from './dataviewlist/dataViewEdit.component';
import { SmDataviewToresComponent } from './dataview/res/tores.component';
import { RbacDataSourceComponent } from './datasource/datasource.component';
import { RbacDataSourceEditComponent } from './datasource/edit/edit.component';

const COMPONENTS = [
  SmSqldefineComponent,
  SmDataviewComponent,
  SmDataviewlistComponent,
  RbacDataSourceComponent,
];
const COMPONENTS_NOROUNT = [
  SmSqldefineEditComponent,
  SmDataviewEditComponent,
  SmBtneditComponent,
  PatternComponent,
  SqlSelectorComponent,
  DataViewEditComponent,
  SmDataviewToresComponent,
  RbacDataSourceEditComponent,
];

@NgModule({
  imports: [SharedModule, SmRoutingModule],
  declarations: [...COMPONENTS, ...COMPONENTS_NOROUNT],
  entryComponents: COMPONENTS_NOROUNT,
  providers: [DataViewListResolver],
})
export class SmModule {}
