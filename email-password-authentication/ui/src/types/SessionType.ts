import { getServerRoot } from "../utils";

export type SessionType = "STUDIO" | "CLIENT";

export function getSessionType(): SessionType {
  return getServerRoot().split("//")[1].startsWith("studio") ? "STUDIO" : "CLIENT";
}
