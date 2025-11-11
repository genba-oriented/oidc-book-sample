import { useEffect, useState } from "react";
import { AuthProvider, AuthProviderProps, useAuth } from "react-oidc-context";

const oidcConfig: AuthProviderProps = {
  authority: "http://localhost:18080/realms/master",
  client_id: "sample-spa",
  redirect_uri: "http://localhost:3000",
  post_logout_redirect_uri: "http://localhost:3000",
  response_type: "code",
  scope: "openid",
};

export const TopPage = () => {
  return (
    <AuthProvider {...oidcConfig}>
      <TopPageContent />
    </AuthProvider>
  );
};

const TopPageContent = () => {
  const auth = useAuth();
  return (
    <div>
      <h1>トップ画面</h1>
      {auth.isAuthenticated ? <AfterLogin /> : <BeforeLogin />}
    </div>
  );
};

const BeforeLogin = () => {
  const auth = useAuth();
  return (
    <div>
      <p>ログインしてください</p>
      <button onClick={() => {
        auth.signinRedirect();
      }}>ログイン</button>
    </div>
  );
};

const AfterLogin = () => {
  const auth = useAuth();
  const [orderItems, setOrderItems] = useState([]);
  useEffect(() => {
    fetch("http://localhost:8080/api/order-items", {
      headers: {
        Authorization: "Bearer " + auth.user.access_token
      }
    }).then(res => res.json()).then(data => setOrderItems(data));
  }, []);
  return (
    <div>
      <p>こんにちは、{auth.user.profile.preferred_username}さん！</p>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>商品名</th>
            <th>数量</th>
            <th>値段</th>
          </tr>
        </thead>
        <tbody>
          {orderItems.map((orderItem, idx) =>
            <tr key={idx}>
              <td>{orderItem.id}</td>
              <td>{orderItem.productName}</td>
              <td>{orderItem.quantity}</td>
              <td>{orderItem.price}</td>
            </tr>
          )}
        </tbody>
      </table>
      <button onClick={() => {
        auth.signoutRedirect();
      }}>ログアウト</button>
    </div>
  );
};
