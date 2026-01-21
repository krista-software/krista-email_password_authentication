import { createContext } from "react";
import { Subject, Observable } from "rxjs";
import { WorkspaceInfo } from "./types/WorkspaceInfo";
import { routes } from "./constants/routes";
import { getUIRoot } from "./utils";

export const contextSubject = new Subject<PageContext>();

export const defaultPageContext: PageContext = {
  onChange: contextSubject,
  navigate: () => {
    console.error("navigate not ready");
    window.location.href = getUIRoot() + "/" + routes.notFound;
  },
  redirectUrl: "",
  workspaceInfo: { workspaceId: "", workspaceName: "" },
  state: "",
};

export const DataContext = createContext<PageContext>(defaultPageContext);

export type PageContext = {
  onChange: Observable<PageContext>;
  email?: string;
  redirectUrl: string;
  navigate: (path: string, keepQuery?: boolean) => void;
  workspaceInfo?: WorkspaceInfo;
  state: string;
};

export function createSearchParam(context: PageContext): string {
  const { email, redirectUrl, state } = context;
  const data: Record<string, string> = {};
  if (email) {
    data["email"] = email;
  }
  if (redirectUrl) {
    data["redirectUrl"] = redirectUrl;
  }
  if (state) {
    data["state"] = state;
  }
  const searchParams = new URLSearchParams(data);
  const search = searchParams.toString();
  return search || "";
}
