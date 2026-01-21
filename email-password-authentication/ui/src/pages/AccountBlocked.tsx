import { useContext, useEffect } from "react";
import { DataContext } from "../context";
import { apiRecoverAccount } from "../api/FetchCalls";
import { routes } from "../constants/routes";
import { getServerRoot } from "../utils";
import { ShowMessage } from "../components/ShowMessage";

export function AccountBlocked() {
  const context = useContext(DataContext);
  useEffect(() => {
    const subscription = context.onChange.subscribe((context) => {
      if (!context.email) {
        context.navigate(routes.notFound);
      }
    });
    return () => subscription.unsubscribe();
  }, []);

  const onSubmit = () => {
    apiRecoverAccount({ emailId: context.email as string, redirectUrl: context.redirectUrl || getServerRoot() }, () => {
      context.navigate(routes.emailSend);
    });
  };

  return <ShowMessage message={"BLOCKED_MSG"} onSubmit={onSubmit} buttonText="RECOVER_ACCOUNT" />;
}
