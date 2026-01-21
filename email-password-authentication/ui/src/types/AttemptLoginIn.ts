import { SessionType } from "./SessionType";

export type AttemptLoginIn = {
  emailId: string;
  password: string;
  clientSessionId?: string;
  loginCode?: string;
  sessionType: SessionType;
};
