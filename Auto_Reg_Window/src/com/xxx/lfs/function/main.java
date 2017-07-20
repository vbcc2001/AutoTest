package com.xxx.lfs.function;

/**
 * Created by Drizzt on 2017-07-18.
 */
public class main {

    public void onClick(String code) {
        String pubkey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2zmsLpmPmamWcjznviihheXtecRJCQXj" +
                "n7rjq5OQscJvK+nK02SAjpSy1GBX4JNVJKLIC9XEtKHsB6pGMXK+C9mHSWYhgF2JwXqylDXPxBZR" +
                "3/JLrJO9awN8Jn9BLMAeXCnpGfuGnzH9RSim9+uXpRBwjbly7YCbWZEY+5n18dDQlXP4QBOyh7jE" +
                "0pKYeXoLkSdgWPxOL5tfuiSjewG06xMW+e2OQDvRFUhOgQM41eP8qF9KFaFduUzEiiQ5zYHUHHxC" +
                "4sqrIHs1HzZJT6701bh4C3JYOAPo/j6qJw3nEtjb+Oo2AVqGcQr5PsGcH9bGoHSXYulrhyZCWQCq" +
                "ioZotQIDAQAB";
        RSAUtils.loadPublicKey(pubkey);
//      RSAUtils.loadPrivateKey(priKey);
        String enctytCode = null;
        try {
            enctytCode = RSAUtils.encryptWithRSA(code);
            String sn = SNUtil.getMD5(enctytCode);
            sn = sn.substring(0, 12);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
