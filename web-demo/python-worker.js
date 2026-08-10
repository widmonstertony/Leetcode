"use strict";

const PYODIDE_BASE = "https://cdn.jsdelivr.net/pyodide/v0.27.7/full/";
let pyodide;

async function initialize() {
  try {
    importScripts(`${PYODIDE_BASE}pyodide.js`);
    pyodide = await loadPyodide({ indexURL: PYODIDE_BASE });
    postMessage({ type: "ready" });
  } catch (error) {
    postMessage({ type: "init-error", error: error?.message || String(error) });
  }
}

async function runSolution({ source, methodName, testCases }) {
  pyodide.globals.set("_lt_source", String(source || ""));
  pyodide.globals.set("_lt_method_name", String(methodName || ""));
  pyodide.globals.set("_lt_test_cases", String(testCases || "[]"));
  try {
    const result = await pyodide.runPythonAsync(`
import contextlib
import io
import json
import traceback

def _leettutor_run(source, method_name, encoded_cases):
    output = io.StringIO()
    cases_out = []
    try:
        cases = json.loads(encoded_cases)
        if not isinstance(cases, list):
            raise ValueError("Test cases must be a JSON array.")
        namespace = {}
        with contextlib.redirect_stdout(output):
            exec(source, namespace)
            solution_type = namespace.get("Solution")
            if solution_type is None:
                raise ValueError("Define a Solution class before running tests.")
            solution = solution_type()
            if not method_name:
                candidates = [
                    name for name in dir(solution)
                    if not name.startswith("_") and callable(getattr(solution, name))
                ]
                if len(candidates) != 1:
                    raise ValueError("Enter the Solution method name.")
                method_name = candidates[0]
            method = getattr(solution, method_name, None)
            if not callable(method):
                raise ValueError(f"Solution.{method_name} is not callable.")
            for case in cases:
                if not isinstance(case, dict) or "args" not in case or "expected" not in case:
                    raise ValueError('Each case needs "args" and "expected".')
                args = case["args"]
                if not isinstance(args, list):
                    raise ValueError('Each case "args" must be an array.')
                actual = method(*args)
                cases_out.append({
                    "passed": actual == case["expected"],
                    "actual": actual,
                    "expected": case["expected"],
                })
        return {
            "passed": sum(1 for case in cases_out if case["passed"]),
            "total": len(cases_out),
            "cases": cases_out,
            "stdout": output.getvalue(),
            "error": "",
        }
    except Exception:
        return {
            "passed": 0,
            "total": len(cases_out),
            "cases": cases_out,
            "stdout": output.getvalue(),
            "error": traceback.format_exc(limit=6),
        }

json.dumps(_leettutor_run(_lt_source, _lt_method_name, _lt_test_cases), ensure_ascii=False)
    `);
    return JSON.parse(result);
  } finally {
    for (const name of ["_lt_source", "_lt_method_name", "_lt_test_cases"]) pyodide.globals.delete(name);
  }
}

self.addEventListener("message", async (event) => {
  const { id } = event.data;
  try {
    const result = await runSolution(event.data);
    postMessage({ type: "result", id, result });
  } catch (error) {
    postMessage({ type: "run-error", id, error: error?.message || String(error) });
  }
});

initialize();
