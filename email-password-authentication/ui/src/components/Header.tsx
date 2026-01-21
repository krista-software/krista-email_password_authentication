import { useContext } from "react";

import logo from "../assets/logo.png";
import { DataContext } from "../context";

export function Header({ hideEmail }: { hideEmail?: boolean }) {
  const data = useContext(DataContext);

  return (
    <div className="flex flex-col items-center">
      <img src={logo} alt="Krista logo" className="h-10 mb-2" />
      {/* <p className="font-medium text-lg mb-2">Krista Client</p> */}
      <p className="font-medium text-lg mb-2">{data.workspaceInfo?.workspaceName || ""}</p>
      {!hideEmail && data.email ? <p className="text-text-secondary text-sm font-medium">{data.email}</p> : null}
    </div>
  );
}
