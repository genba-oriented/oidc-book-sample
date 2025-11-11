import express, { Request, Response } from 'express';
import { auth } from 'express-openid-connect';

export const app = express();

app.use(auth({
  authRequired: false,
  issuerBaseURL: 'http://localhost:18080/realms/master',
  baseURL: 'http://localhost:3000',
  clientID: 'sample-bff',
  clientSecret: 'D51UWU8Kl9Z3nlynAecGouTh3oRHhYrr',
  secret: "secret-for-session",
  authorizationParams: {
    response_type: 'code',
    scope: "openid",
  },
  idpLogout: true,
}));

app.get("/me", (req: Request, res: Response) => {
  if (req.oidc.isAuthenticated()) {
    res.json({
      name: req.oidc.user.preferred_username
    });
  } else {
    res.json({
      name: null
    });
  }
});

app.get("/api/order-items", async (req: Request, res: Response) => {
  console.log(req.oidc.refreshToken);
  let { access_token, isExpired, refresh } = req.oidc.accessToken;
  if (isExpired()) {
    console.log("refreshing access token");
    ({ access_token } = await refresh());
  }
  const data = await fetch("http://localhost:8080/api/order-items", {
    headers: {
      Authorization: "Bearer " + access_token,
    }
  }).then(res => res.json());
  res.json(data);
});

