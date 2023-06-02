# SPDX-FileCopyrightText: 2023-present imbus AG
#
# SPDX-License-Identifier: Apache-2.0
# pyright: reportMissingImports=false, reportUnusedVariable=warning
from __future__ import annotations

import importlib
import os
import sys
import warnings
from os import PathLike
from pathlib import Path
from typing import Any, Callable, Dict, List, Optional, Sequence, TextIO, Tuple, Union

import jpype
import jpype.imports
from jpype.types import JFloat, JInt, JString

from .__about__ import __version__


class KeywordInvocationError(Exception):
    ROBOT_SUPPRESS_NAME = True


def convert_to_java(value: Any) -> Any:
    import java

    if isinstance(value, str):
        return JString(value)
    if isinstance(value, bool):
        return value
    if isinstance(value, int):
        return JInt(value)
    if isinstance(value, float):
        return JFloat(value)
    if isinstance(value, float):
        return JFloat(value)
    if isinstance(value, (list, tuple, set)):
        return java.util.ArrayList([convert_to_java(a) for a in value])
    if isinstance(value, dict):
        result = java.util.LinkedHashMap()
        for k, v in value.items():
            result.put(JString(k), convert_to_java(v))
        return result

    return value


def convert_to_python(value: Any) -> Any:
    import java

    if isinstance(value, JString):
        return str(value)

    if isinstance(value, java.lang.Integer):
        return int(value)

    if isinstance(value, (java.lang.Double, java.lang.Float)):
        return float(value)

    return value


class classproperty(object):  # noqa: N801
    def __init__(self, f: Callable[..., Any]) -> None:
        self.f = f

    def __get__(self, _obj: Any, owner: Any) -> Any:
        return self.f(owner)


class JavaLibraryBase:
    """Base class for Java libraries."""

    def __init__(
        self,
        java_class_name: str,
        class_path: Union[str, Sequence[Union[str, PathLike[str]]], None] = None,
        *args: Any,
        **kwargs: Any,
    ) -> None:
        JavaLibraryBase._init_jype()

        if class_path is not None:
            for v in [class_path] if isinstance(class_path, str) else class_path:
                jpype.addClassPath(str(v))

        self._java_class = importlib.import_module(java_class_name)

        self._java_instance = self._java_class(*convert_to_java(args), **convert_to_java(kwargs))  # type: ignore

    __jype_initialized: bool = False
    __std_capture_initialized: bool = False
    __library_helper: Any = None

    @staticmethod
    def _get_library_helper() -> Any:
        if JavaLibraryBase.__library_helper is None:
            JavaLibraryBase._init_jype()
            JavaLibraryBase.__library_helper = importlib.import_module(
                "de.imbus.robotframework.helper.RobotLibraryHelper"
            )
        return JavaLibraryBase.__library_helper

    @classproperty
    def ROBOT_LIBRARY_SCOPE(cls) -> str:  # noqa: N802
        # TODO: helper = cls._get_library_helper()
        return "GLOBAL"

    @staticmethod
    def _init_jype() -> None:
        if not JavaLibraryBase.__jype_initialized:
            JavaLibraryBase.__jype_initialized = True

            env_extra_args = os.environ.get("JPYPE_JVM_EXTRA_ARGS", "")
            extra_args = env_extra_args.split() if env_extra_args else []

            jpype.startJVM(
                jpype.getDefaultJVMPath(),
                "-ea",
                "-Dfile.encoding=UTF-8",
                *extra_args
                # "-Xint",
                # "-Xdebug",
                # "-Xnoagent",
                # "-Xrunjdwp:transport=dt_socket,server=y,address=12999,suspend=n",
            )

            class_path = [
                f
                for f in Path(__file__)
                .parent.joinpath("java")
                .glob(f"robotframework-javalibrarybase-{__version__}*.jar")
            ]

            if not class_path:
                if any(
                    f
                    for f in Path(__file__).parent.parent.parent.glob(
                        "target/classes/de/imbus/robotframework/**/*.class"
                    )
                ):
                    class_path = [Path(__file__).parent.parent.parent.joinpath("target", "classes")]

            for v in class_path:
                jpype.addClassPath(str(v))

            JavaLibraryBase._enable_stdout_stderr_capture()

    @staticmethod
    def _enable_stdout_stderr_capture() -> None:
        if not JavaLibraryBase.__std_capture_initialized:
            JavaLibraryBase.__std_capture_initialized = True
            try:
                from de.imbus.robotframework.helper import OutputStreamWriter
                from java.io import PrintStream
                from java.lang import System
                from java.nio.charset import StandardCharsets

                @jpype.JImplements("de.imbus.robotframework.helper.Writer")
                class Writer:
                    def __init__(self, text_io: Callable[[], TextIO]) -> None:
                        self.text_io = text_io

                    @jpype.JOverride
                    def write(self, b: int) -> None:
                        self.text_io().write(chr(b))

                    @jpype.JOverride
                    def writeBytes(self, b: Any, off: int, len: int) -> None:  # NOSONAR  # noqa: N802
                        try:
                            self.text_io().write(bytes(b[off : off + len]).decode("utf-8"))
                        except BaseException as e:
                            print(e)
                            raise

                    @jpype.JOverride
                    def flush(self) -> None:
                        try:
                            self.text_io().flush()
                        except BaseException as e:
                            print(e)
                            raise

                System.setOut(
                    PrintStream(
                        OutputStreamWriter(Writer(lambda: sys.stdout)),
                        True,
                        StandardCharsets.UTF_8.name(),
                    )
                )
                System.setErr(
                    PrintStream(
                        OutputStreamWriter(Writer(lambda: sys.stderr)),
                        True,
                        StandardCharsets.UTF_8.name(),
                    )
                )
            except (SystemExit, KeyboardInterrupt):
                raise
            except BaseException as e:
                warnings.warn(f"Failed to enable stdout/stderr capture: ({type(e)}: {e})")

    def get_keyword_names(self) -> List[str]:
        kws = self._get_library_helper().getKeywordNames(self._java_instance)
        return [str(v) for v in kws]

    def get_keyword_arguments(self, name: str) -> List[str]:
        return [str(v) for v in self._get_library_helper().getKeywordArguments(self._java_instance, name)]

    def get_keyword_types(self, name: str) -> List[str]:
        return [str(v) for v in self._get_library_helper().getKeywordTypes(self._java_instance, name)]

    def get_keyword_documentation(self, name: str) -> Optional[str]:
        if name == "__intro__":
            return str(self._java_class.__doc__)
        result = self._get_library_helper().getKeywordDocumentation(self._java_instance, name)
        if result is None:
            return None
        return str(result)

    def run_keyword(self, name: str, args: Tuple[Any], kwargs: Dict[str, Any]) -> Any:
        import de.imbus.robotframework.exceptions  # type: ignore[import]
        from java.lang.reflect import InvocationTargetException

        try:
            return convert_to_python(
                self._get_library_helper().runKeyword(
                    self._java_instance, JString(name), convert_to_java(args), convert_to_java(kwargs)
                )
            )
        except InvocationTargetException as e:
            if e.getCause() is None:
                raise

            if isinstance(e.getCause(), de.imbus.robotframework.exceptions.RobotFrameworkException):
                exception = self.__get_robot_exception_class(e)

                raise exception(str(e.getCause().getMessage()), bool(e.getCause().isHtml())) from e

            raise KeywordInvocationError(f"{e.getCause().getClass().getName()}: {e.getCause().getMessage()}") from e

        except de.imbus.robotframework.exceptions.RobotFrameworkException as e:
            exception = self.__get_robot_exception_class(e)

            raise exception(str(e.getMessage()), bool(e.isHtml())) from e

    def __get_robot_exception_class(self, exception: Any) -> Callable[[str, bool], BaseException]:
        import de.imbus.robotframework.exceptions
        import robot.api.exceptions

        result: Callable[[str, bool], BaseException] = robot.api.exceptions.Error

        if isinstance(exception.getCause(), de.imbus.robotframework.exceptions.ContinuableFailure):
            result = robot.api.exceptions.ContinuableFailure
        elif isinstance(exception.getCause(), de.imbus.robotframework.exceptions.Error):
            result = robot.api.exceptions.Error
        elif isinstance(exception.getCause(), de.imbus.robotframework.exceptions.Failure):
            result = robot.api.exceptions.Failure
        elif isinstance(exception.getCause(), de.imbus.robotframework.exceptions.FatalError):
            result = robot.api.exceptions.FatalError
        elif isinstance(exception.getCause(), de.imbus.robotframework.exceptions.SkipExecution):
            result = robot.api.exceptions.SkipExecution

        return result
