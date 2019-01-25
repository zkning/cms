import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AccountcenterComponent } from './accountcenter/accountcenter.component';
import { UpdatePasswordComponent } from './password/updatepassword.component';
import { RbacGrouplistComponent } from './grouplist/grouplist.component';
import { RbacPermitlistComponent } from './permitlist/permitlist.component';
import { RbacDictComponent } from './dict/dict.component';

const routes: Routes = [
  { path: 'accountcenter', component: AccountcenterComponent },
  { path: 'updatePassword', component: UpdatePasswordComponent },
  { path: 'grouplist', component: RbacGrouplistComponent },
  { path: 'permitlist', component: RbacPermitlistComponent },
  { path: 'dict', component: RbacDictComponent },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RbacRoutingModule {}
