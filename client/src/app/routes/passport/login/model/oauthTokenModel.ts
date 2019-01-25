import { UserInfo } from './credentialsModel';
export class OauthTokenModel {
  constructor(parameters) {}
  access_token: string;
  token_type: string;
  refresh_token: string;
  expires_in: number;
  scope: string;
  jti: string;
  user: UserInfo;
  error: string;
  error_description: string;
}
