import { Alert, AlertColor, Button, IconButton, Snackbar } from "@mui/material";
import React, { ReactElement, useEffect, useState } from "react";
import CloseIcon from "@mui/icons-material/Close";
import { v4 as uuidv4 } from "uuid";
import { Subject } from "rxjs";

export function showError(message: string) {
  showSnackBar({ message, severity: "error" });
}

export function showSnackBar(props: SnackbarData) {
  snackBarSubject.next(props);
}

type SnackbarData = {
  message: string;
  severity: AlertColor;
};

const snackBarSubject = new Subject<SnackbarData>();

export function SnackbarComponent() {
  const [snackbBarItems, setSnackBarItems] = useState([] as ReactElement[]);

  useEffect(() => {
    snackBarSubject.subscribe((data) => {
      const key = uuidv4();
      const close = () => {
        const newItems = snackbBarItems.filter((e) => e.key !== key);
        setSnackBarItems(newItems);
      };
      const item = <SnackBarItem key={key} message={data.message} severity={data.severity} close={close} />;
      const newItems = [...snackbBarItems];
      newItems.unshift(item);
      setSnackBarItems(newItems);
    });
  }, []);

  return snackbBarItems;
}

function SnackBarItem({
  key,
  message,
  severity,
  close,
}: {
  key: string;
  message: string;
  severity: AlertColor;
  close: () => any;
}) {
  const action = (
    <React.Fragment>
      <Button color="secondary" size="small" onClick={close}>
        UNDO
      </Button>
      <IconButton size="small" aria-label="close" color="inherit" onClick={close}>
        <CloseIcon fontSize="small" />
      </IconButton>
    </React.Fragment>
  );
  return (
    <Snackbar
      key={key}
      anchorOrigin={{ horizontal: "center", vertical: "bottom" }}
      open={true}
      action={action}
      autoHideDuration={4000}
      onClose={close}
    >
      <Alert onClose={close} severity={severity} sx={{ width: "100%" }}>
        {message}
      </Alert>
    </Snackbar>
  );
}
