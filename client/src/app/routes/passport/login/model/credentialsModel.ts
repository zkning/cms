import { Menu } from '@delon/theme';

export class CredentialsModel {
  constructor(parameters) {}
  app: App;
  user: UserInfo;
  menu: Array<Menu>;
  token: string;
  permits: string[];
}

export class App {
  constructor(parameters) {}
  name: string;
  description: string;
}

export class UserInfo {
  constructor(parameters) {}
  username: string;
  name: string;
  email: string;
  avatar: string;
  id: string;
}
