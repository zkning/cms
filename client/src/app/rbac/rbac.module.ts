import { NgModule } from '@angular/core';
import { SharedModule } from '@shared/shared.module';
import { RbacRoutingModule } from './rbac-routing.module';
import { AccountcenterComponent } from './accountcenter/accountcenter.component';
import { UpdatePasswordComponent } from './password/updatepassword.component';
import { RbacGrouplistComponent } from './grouplist/grouplist.component';
import { RbacUsereditComponent } from './useredit/useredit.component';
import { RbacPermitlistComponent } from './permitlist/permitlist.component';
import { RbacRoleeditComponent } from './roleedit/roleedit.component';
import { RbacResourceseditComponent } from './resourcesedit/resourcesedit.component';
import { RbacUserlistComponent } from './userlist/userlist.component';
import { RbacDictComponent } from './dict/dict.component';
import { RbacDictEditComponent } from './dict/edit/edit.component';
import { RbacGroupeditComponent } from './grouplist/groupedit.component';

const COMPONENTS = [
  RbacGrouplistComponent,
  RbacPermitlistComponent,
  RbacDictComponent,
];
const COMPONENTS_NOROUNT = [
  AccountcenterComponent,
  UpdatePasswordComponent,
  RbacUsereditComponent,
  RbacRoleeditComponent,
  RbacResourceseditComponent,
  RbacUserlistComponent,
  RbacDictEditComponent,
  RbacGroupeditComponent,
];

@NgModule({
  imports: [SharedModule, RbacRoutingModule],
  declarations: [...COMPONENTS, ...COMPONENTS_NOROUNT],
  entryComponents: COMPONENTS_NOROUNT,
})
export class RbacModule {}
