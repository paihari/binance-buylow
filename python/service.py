from com.gundi.binance.buylow.service import HelloService

class HelloServicePython(HelloService):
    def __init__(self):
        self.value="Hello from python"

    def getHello(self):
        return self.value