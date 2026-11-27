"""Execute com Python 3 e ECOCODE_TEST_URL apontando para um servidor de teste."""
import json
import os
import secrets
import unittest
from http.cookiejar import CookieJar
from urllib.error import HTTPError
from urllib.request import HTTPCookieProcessor, Request, build_opener


class Navegador:
    def __init__(self):
        self.http = build_opener(HTTPCookieProcessor(CookieJar()))

    def pedir(self, rota, dados=None, metodo=None):
        corpo = None if dados is None else json.dumps(dados).encode()
        req = Request(os.environ["ECOCODE_TEST_URL"].rstrip("/") + "/api/" + rota,
                      data=corpo, method=metodo,
                      headers={"X-EcoCode": "web", "Content-Type": "application/json"})
        try:
            resposta = self.http.open(req, timeout=15)
        except HTTPError as erro:
            resposta = erro
        with resposta:
            texto = resposta.read()
            return resposta.status, json.loads(texto) if texto else None


def conta_teste():
    cpf = str(secrets.randbelow(900_000_000) + 100_000_000)
    for tamanho in (9, 10):
        soma = sum(int(d) * (tamanho + 1 - i) for i, d in enumerate(cpf))
        cpf += str((soma * 10 % 11) % 10)
    return dict(nome="Pessoa de Teste", cpf=cpf, email=cpf + "@example.org",
                senha=secrets.token_urlsafe(18), nascimento="01/01/2000", bairro="SETOR_BUENO")


class FluxoWeb(unittest.TestCase):
    def cadastrar_e_entrar(self):
        cliente = Navegador()
        conta = conta_teste()
        self.assertEqual(cliente.pedir("contas", conta)[0], 200)
        status, perfil = cliente.pedir("login", {k: conta[k] for k in ("email", "senha")})
        self.assertEqual(status, 200)
        self.assertEqual(perfil["cargo"], "USUARIO")
        self.assertNotIn("senhaCriptografada", perfil)
        return cliente

    def test_descarte_e_controle_de_acesso(self):
        dono = self.cadastrar_e_entrar()
        outro = self.cadastrar_e_entrar()
        status, residuo = dono.pedir("residuos", dict(tipo="CABOS", quantidade=2, descricao="Cabo USB"))
        self.assertEqual(status, 200)
        self.assertEqual(int(residuo["codigo"]) % 5, 2)
        pedido = {"residuoId": residuo["id"]}
        self.assertEqual(outro.pedir("pedidos", pedido)[0], 403)
        status, criado = dono.pedir("pedidos", pedido)
        self.assertEqual(status, 200)
        self.assertEqual(criado["status"], "PENDENTE")
        self.assertEqual(dono.pedir("pedidos", pedido)[0], 400)
        self.assertEqual(len(dono.pedir("pedidos")[1]), 1)
        self.assertEqual(outro.pedir("pedidos")[1], [])
        cancelar = "pedidos/" + criado["id"] + "/cancelar"
        self.assertEqual(outro.pedir(cancelar, {})[0], 403)
        self.assertEqual(dono.pedir("pedidos/proximo", {})[0], 403)
        self.assertEqual(dono.pedir("log")[0], 403)
        self.assertEqual(dono.pedir(cancelar, {})[1]["status"], "CANCELADA")
        self.assertEqual(dono.pedir("residuos")[1][0]["status"], "DISPONIVEL")
        self.assertEqual(dono.pedir("logout", {})[0], 200)
        self.assertEqual(dono.pedir("perfil")[0], 401)


if __name__ == "__main__":
    unittest.main()
