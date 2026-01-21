import { useEffect, useState } from "react";
import { Outlet, useNavigate, useSearchParams } from "react-router-dom";
import { DataContext, PageContext, contextSubject, createSearchParam, defaultPageContext } from "./context";
import { apiGetWorkspaceInfo } from "./api/FetchCalls";
import { routes } from "./constants/routes";
import { getUIPath, getUIRoot } from "./utils";
import { SnackbarComponent } from "./components/SnackBarComponent";

export function App() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [data, setData] = useState<PageContext>(defaultPageContext);

  useEffect(() => {
    const subscription = contextSubject.subscribe((context) => {
      setData(context);
    });
    return () => subscription.unsubscribe();
  }, []);

  useEffect(() => {
    const email = searchParams.get("email") || "";
    const redirectUrl = searchParams.get("redirectUrl") || "";
    const state = searchParams.get("state") || "";
    const nv = (path: string, keepQuery = true) => {
      if (path === routes.notFound) {
        const notFoundUrl = `${getUIRoot()}/${routes.notFound}`;
        window.location.replace(notFoundUrl);
      }
      if (path.startsWith("http")) {
        const url = new URL(path);
        if (state) {
          url.searchParams.set("state", state)
        }
        window.location.href = url.toString();
      } else if (keepQuery) {
        const search = createSearchParam(data) || searchParams.toString();
        navigate(`${getUIPath()}/${path}?${search}`);
      } else {
        navigate(`${getUIPath()}/${path}`);
      }
    };
    apiGetWorkspaceInfo((info) => {
      const initialContext = {
        email,
        redirectUrl,
        onChange: contextSubject,
        navigate: nv,
        workspaceInfo: info,
        state,
      };
      contextSubject.next(initialContext);
    });
  }, []);

  return (
    <DataContext.Provider value={data}>
      <Outlet />
      <SnackbarComponent />
    </DataContext.Provider>
  );
}
