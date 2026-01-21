import { showError } from "../components/SnackBarComponent";
import { apiCodes } from "../constants/apiCodes";
import { ChangePasswordIn } from "../types/ChangePasswordIn";
import { RecoverAccountIn } from "../types/RecoverAccountIn";
import { ResetPasswordIn } from "../types/ResetPasswordIn";
import { ResetPasswordOut } from "../types/ResetPasswordOut";
import { SendForgotLinkIn } from "../types/SendForgotLinkIn";
import { UserBlockedOut } from "../types/UserBlockedOut";
import { WorkspaceInfo } from "../types/WorkspaceInfo";
import { getServerRoot } from "../utils";

export function callApi<I, O, E>(
  path: string,
  method: "GET" | "POST",
  input: I,
  onDone: (o: O) => any,
  onError: (e: E) => any = () => 0
): void {
  const realPath = `${getServerRoot()}/api/${path}`;
  fetch(realPath, {
    method,
    body: JSON.stringify(input),
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => {
      const contentType = response.headers.get("content-type");
      const isJsonContent = contentType && contentType.indexOf("application/json") !== -1;
      if (response.status === 200) {
        if (isJsonContent) {
          response.json().then((j) => {
            onDone(j);
          });
        } else {
          onDone(null as O);
        }
      } else {
        if (isJsonContent) {
          response.json().then((j) => {
            if (typeof j === "object" && j["errorMessage"]) {
              showError(j["errorMessage"]);
            }
            onError(j);
          });
        } else {
          showError("Something went wrong");
          onError(null as E);
        }
      }
    })
    .catch(() => console.error(`call to ${realPath} failed`));
}

export function apiGetWorkspaceInfo(onDone: (_: WorkspaceInfo) => any) {
  callApi(apiCodes.workspaceInfo, "GET", undefined, onDone, undefined);
}

export function apiRecoverAccount(input: RecoverAccountIn, onDone: () => any) {
  callApi(apiCodes.recoverAccount, "POST", input, onDone);
}

export function apiSendForgotLink(input: SendForgotLinkIn, onDone: () => any) {
  callApi(apiCodes.sendForgotLink, "POST", input, onDone);
}

export function apiIsUserBlocked(emailId: string, onDone: (_: UserBlockedOut) => any) {
  callApi(apiCodes.isUserBlocked, "POST", { emailId }, onDone);
}

export function apiResetPassword(input: ResetPasswordIn, onDone: (out: ResetPasswordOut) => any) {
  callApi(apiCodes.resetPassword, "POST", input, onDone);
}

export function apiChangePassword(input: ChangePasswordIn, onDone: () => any) {
  callApi(apiCodes.changePassword, "POST", input, onDone);
}
