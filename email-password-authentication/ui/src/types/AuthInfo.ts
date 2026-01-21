export type AuthInfo = {
  clientSessionId: string;
  loginCode: string;
};

export type AuthInfoOut = {
  clientSessionId: string;
  loginCode: string;
  workspaceId: string;
};
