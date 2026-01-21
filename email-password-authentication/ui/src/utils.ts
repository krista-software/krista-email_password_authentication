export function getServerRoot(): string {
  if (process.env.NODE_ENV === "development") {
    return window.location.origin;
  } else {
    const url = new URL(window.location.href);
    const pathname = url.pathname;
    const uiIndex = pathname.indexOf("/ui");
    return window.location.origin + pathname.substring(0, uiIndex);
  }
}

export const AUTH_INFO_COOKIE_IN = "--auth-extension-input";
export const AUTH_INFO_COOKIE_OUT = "--auth-extension-output";

export function getCookie(name: string): string | undefined {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop()?.split(";").shift();
}

export function setCookie(name: string, value: string | undefined = undefined, days: number | undefined = 400) {
  var expires = "";
  if (days) {
    var date = new Date();
    date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
    expires = "; expires=" + date.toUTCString();
  }
  document.cookie = name + "=" + (value || "") + expires + "; path=/";
}

export function getUIPath() {
  if (process.env.NODE_ENV === "development") {
    return "";
  }
  return getServerRoot().substring(window.location.origin.length) + "/ui";
}

export function getUIRoot() {
  if (process.env.NODE_ENV === "development") {
    return getServerRoot();
  }
  return getServerRoot() + "/ui";
}

export const goBack = () => window.history.back();
