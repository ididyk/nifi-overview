import datetime
import json
import sys
import traceback

from java.nio.charset import StandardCharsets
from org.apache.commons.io import IOUtils
from org.apache.nifi.processor.io import StreamCallback
from org.python.core.util import StringUtil


class TransformCallback(StreamCallback):
    def __init__(self):
        pass

    def process(self, input, output):
        try:
            input_text = IOUtils.toString(input, StandardCharsets.UTF_8)
            record = json.loads(input_text)

            values = []
            fields = []
            for k, v in record.items():
                fields.append(k)
                values.append("'" + str(v) + "'")

            query = "INSERT INTO albums ({}) VALUES ({}); ".format(', '.join(fields), ', '.join(values))

            output.write(StringUtil.toBytes(query))
        except:
            traceback.print_exc(file=sys.stdout)
            raise


flowFiles = session.get(5000)
if not flowFiles.isEmpty():
    for flowFile in flowFiles:
        if flowFile is not None:
            flow = session.write(flowFile, TransformCallback())
            session.transfer(flow, REL_SUCCESS)
